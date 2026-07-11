package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.NghiPhepRequest;
import com.company.hrm.attendance.dto.NghiPhepResponse;
import com.company.hrm.attendance.entity.LoaiNghiPhep;
import com.company.hrm.attendance.entity.NghiPhep;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.repository.NghiPhepRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Nghiệp vụ đơn nghỉ phép (T10) với quy trình duyệt 2 cấp:
 * {@code CHO_DUYET → DUYET_CAP_1 (MANAGER) → DA_DUYET (HR)}, hoặc {@code TU_CHOI} tại bất kỳ cấp.
 *
 * <p>Quy tắc tính {@code soNgayNghi}: đếm số ngày NV có phân ca trong khoảng {@code [tuNgay, denNgay]}.
 * NV không có ca nào trong khoảng → {@code 0} ngày → lỗi {@code NGHI_PHEP_NO_SHIFT}.
 *
 * <p>Trừ quỹ phép (với {@link LoaiNghiPhep#PHEP_NAM}) chỉ khi đạt {@code DA_DUYET}.
 * Hủy đơn sau duyệt → hoàn quỹ (gọi {@link QuyPhepService#hoanDaDung}).
 *
 * <p>Cùng người duyệt cả 2 cấp → {@code APPROVER_DUPLICATE}.
 */
@Service
public class NghiPhepService {

    private static final Logger log = LoggerFactory.getLogger(NghiPhepService.class);
    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

    /** Trạng thái NV được phép nghỉ phép (tương tự phân ca). */
    private static final Set<TrangThaiNv> ALLOWED_STATUS = EnumSet.of(
            TrangThaiNv.UNG_VIEN, TrangThaiNv.THU_VIEC, TrangThaiNv.CHINH_THUC, TrangThaiNv.TAM_HOAN_HDLD);

    /** Trạng thái đơn hiệu lực — chặn tạo đơn trùng ngày. */
    private static final List<TrangThaiDon> ACTIVE_STATUSES = List.of(
            TrangThaiDon.CHO_DUYET, TrangThaiDon.DUYET_CAP_1, TrangThaiDon.DA_DUYET);

    private final NghiPhepRepository repo;
    private final NhanVienRepository nvRepo;
    private final PhanCaRepository phanCaRepo;
    private final QuyPhepService quyPhepService;

    public NghiPhepService(NghiPhepRepository repo,
                           NhanVienRepository nvRepo,
                           PhanCaRepository phanCaRepo,
                           QuyPhepService quyPhepService) {
        this.repo = repo;
        this.nvRepo = nvRepo;
        this.phanCaRepo = phanCaRepo;
        this.quyPhepService = quyPhepService;
    }

    // ---------------- create ----------------

    @Transactional
    public NghiPhepResponse create(NghiPhepRequest req) {
        validate(req);

        NhanVien nv = nvRepo.findById(req.getNhanVienId()).orElseThrow(() ->
                new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        if (!ALLOWED_STATUS.contains(nv.getTrangThai())) {
            throw new BusinessException("EMPLOYEE_NOT_WORKING",
                    "Nhân viên đang ở trạng thái " + nv.getTrangThai()
                            + ", không thể tạo đơn nghỉ phép");
        }

        if (repo.existsByNhanVienIdAndTrangThaiInAndTuNgayLessThanEqualAndDenNgayGreaterThanEqual(
                req.getNhanVienId(), ACTIVE_STATUSES,
                req.getDenNgay(), req.getTuNgay())) {
            throw new BusinessException("NGHI_PHEP_TRUNG",
                    "Đã có đơn nghỉ phép hiệu lực trùng khoảng ngày ["
                            + req.getTuNgay() + ", " + req.getDenNgay() + "]");
        }

        BigDecimal soNgay = tinhSoNgayNghi(req.getNhanVienId(), req.getTuNgay(), req.getDenNgay());
        if (soNgay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("NGHI_PHEP_NO_SHIFT",
                    "Nhân viên không có phân ca nào trong khoảng ["
                            + req.getTuNgay() + ", " + req.getDenNgay()
                            + "]; không thể tạo đơn nghỉ");
        }

        // PHEP_NAM: kiểm tra quỹ tạm thời có thể cover (không trừ).
        if (req.getLoaiNghiPhep() == LoaiNghiPhep.PHEP_NAM) {
            int nam = req.getTuNgay().getYear();
            BigDecimal conLai = quyPhepService.getBalance(req.getNhanVienId(), nam) != null
                    ? safeConLai(quyPhepService.getBalance(req.getNhanVienId(), nam))
                    : null;
            if (conLai != null && conLai.compareTo(soNgay) < 0) {
                throw new BusinessException("QUY_PHEP_NOT_ENOUGH_PRECHECK",
                        "Quỹ phép còn lại " + conLai + " ngày, không đủ cho yêu cầu " + soNgay
                                + " ngày (sẽ trừ khi đơn được duyệt hoàn toàn)");
            }
        }

        NghiPhep e = new NghiPhep();
        e.setNhanVienId(req.getNhanVienId());
        e.setLoaiNghiPhep(req.getLoaiNghiPhep());
        e.setTuNgay(req.getTuNgay());
        e.setDenNgay(req.getDenNgay());
        e.setSoNgayNghi(soNgay);
        e.setLyDo(req.getLyDo());
        e.setFileDinhKemUrl(req.getFileDinhKemUrl());
        e.setTrangThai(TrangThaiDon.CHO_DUYET);
        NghiPhep saved = repo.save(e);
        log.info("Tạo đơn nghỉ phép NV {} loại {} [{} → {}] = {} ngày",
                req.getNhanVienId(), req.getLoaiNghiPhep(),
                req.getTuNgay(), req.getDenNgay(), soNgay);
        return NghiPhepResponse.from(saved);
    }

    // ---------------- approve 2 cấp ----------------

    /** Cấp 1 = MANAGER duyệt. */
    @Transactional
    public NghiPhepResponse approveCap1(UUID id, UUID approverId, boolean approve, String ghiChu) {
        NghiPhep e = requireById(id);
        if (e.getTrangThai() != TrangThaiDon.CHO_DUYET) {
            throw new BusinessException("NGHI_PHEP_INVALID_STATE",
                    "Đơn phải ở trạng thái CHO_DUYET để duyệt cấp 1 (hiện tại: "
                            + e.getTrangThai() + ")");
        }
        e.setDuyetCap1Boi(approverId);
        e.setDuyetCap1Luc(OffsetDateTime.now(DEFAULT_OFFSET));
        if (approve) {
            e.setTrangThai(TrangThaiDon.DUYET_CAP_1);
        } else {
            e.setTrangThai(TrangThaiDon.TU_CHOI);
            // TU_CHOI ở cấp 1 → không trừ quỹ
        }
        appendGhiChu(e, ghiChu, approve, 1);
        log.info("NV {} duyệt cấp 1 đơn nghỉ {} → {}", approverId, id, e.getTrangThai());
        return NghiPhepResponse.from(repo.save(e));
    }

    /** Cấp 2 = HR duyệt. Trừ quỹ nếu PHEP_NAM và đạt DA_DUYET. */
    @Transactional
    public NghiPhepResponse approveCap2(UUID id, UUID approverId, boolean approve, String ghiChu) {
        NghiPhep e = requireById(id);
        if (e.getTrangThai() != TrangThaiDon.DUYET_CAP_1) {
            throw new BusinessException("NGHI_PHEP_INVALID_STATE",
                    "Đơn phải ở trạng thái DUYET_CAP_1 để duyệt cấp 2 (hiện tại: "
                            + e.getTrangThai() + ")");
        }
        // Cùng người duyệt cả 2 cấp
        if (approverId != null && approverId.equals(e.getDuyetCap1Boi())) {
            throw new BusinessException("APPROVER_DUPLICATE",
                    "Người duyệt cấp 2 không được trùng với người duyệt cấp 1");
        }
        e.setDuyetCap2Boi(approverId);
        e.setDuyetCap2Luc(OffsetDateTime.now(DEFAULT_OFFSET));
        if (approve) {
            e.setTrangThai(TrangThaiDon.DA_DUYET);
            // Trừ quỹ nếu PHEP_NAM
            if (e.getLoaiNghiPhep() == LoaiNghiPhep.PHEP_NAM) {
                quyPhepService.congDaDung(e.getNhanVienId(),
                        e.getTuNgay().getYear(), e.getSoNgayNghi());
            }
        } else {
            e.setTrangThai(TrangThaiDon.TU_CHOI);
        }
        appendGhiChu(e, ghiChu, approve, 2);
        log.info("NV {} duyệt cấp 2 đơn nghỉ {} → {}", approverId, id, e.getTrangThai());
        return NghiPhepResponse.from(repo.save(e));
    }

    // ---------------- cancel ----------------

    /**
     * NV tạo hoặc HR hủy đơn. Cho phép hủy ở mọi trạng thái trừ HUY (đã hủy rồi).
     * Nếu đã DA_DUYET mà hủy → hoàn quỹ (chỉ PHEP_NAM).
     */
    @Transactional
    public NghiPhepResponse cancel(UUID id) {
        NghiPhep e = requireById(id);
        if (e.getTrangThai() == TrangThaiDon.HUY) {
            throw new BusinessException("NGHI_PHEP_DA_HUY", "Đơn đã hủy");
        }
        boolean daDuyet = e.getTrangThai() == TrangThaiDon.DA_DUYET;
        if (daDuyet && e.getLoaiNghiPhep() == LoaiNghiPhep.PHEP_NAM) {
            quyPhepService.hoanDaDung(e.getNhanVienId(),
                    e.getTuNgay().getYear(), e.getSoNgayNghi());
        }
        e.setTrangThai(TrangThaiDon.HUY);
        log.info("Hủy đơn nghỉ phép {} (đã duyệt: {}, hoàn quỹ: {})",
                id, daDuyet, daDuyet && e.getLoaiNghiPhep() == LoaiNghiPhep.PHEP_NAM);
        return NghiPhepResponse.from(repo.save(e));
    }

    // ---------------- queries ----------------

    @Transactional(readOnly = true)
    public NghiPhepResponse get(UUID id) {
        return NghiPhepResponse.from(requireById(id));
    }

    @Transactional(readOnly = true)
    public List<NghiPhepResponse> list(UUID employeeId, List<TrangThaiDon> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return repo.findAll().stream()
                    .filter(n -> employeeId == null || n.getNhanVienId().equals(employeeId))
                    .map(NghiPhepResponse::from)
                    .toList();
        }
        return repo.findByNhanVienIdAndTrangThaiInOrderByTuNgayDesc(employeeId, statuses)
                .stream().map(NghiPhepResponse::from).toList();
    }

    /** Query theo NV + tháng (tu_ngay ∈ tháng) — dùng cho T11. */
    @Transactional(readOnly = true
    )
    public List<NghiPhepResponse> listByMonth(UUID employeeId, int thang, int nam) {
        LocalDate from = LocalDate.of(nam, thang, 1);
        LocalDate to = from.plusMonths(1).minusDays(1);
        return repo.findByNhanVienIdAndTuNgayBetweenOrderByTuNgayAsc(employeeId, from, to)
                .stream().map(NghiPhepResponse::from).toList();
    }

    // ---------------- helpers ----------------

    private void validate(NghiPhepRequest req) {
        if (req.getDenNgay().isBefore(req.getTuNgay())) {
            throw new BusinessException("NGHI_PHEP_NGAY_INVALID",
                    "denNgay phải >= tuNgay");
        }
        if (req.getTuNgay().isBefore(LocalDate.now())) {
            throw new BusinessException("NGHI_PHEP_PAST",
                    "Không thể tạo đơn nghỉ phép cho ngày trong quá khứ");
        }
        if ((req.getLoaiNghiPhep() == LoaiNghiPhep.OM
                || req.getLoaiNghiPhep() == LoaiNghiPhep.THAI_SAN)
                && (req.getFileDinhKemUrl() == null || req.getFileDinhKemUrl().isBlank())) {
            throw new BusinessException("NGHI_PHEP_REQUIRED_ATTACHMENT",
                    "Loại " + req.getLoaiNghiPhep() + " yêu cầu file đính kèm (chứng từ y tế)");
        }
    }

    /**
     * Tính số ngày nghỉ theo lịch phân ca của NV trong khoảng {@code [tuNgay, denNgay]}.
     * Mỗi ngày có phân ca tính 1 ngày (kể cả ca ngắn hạn hoặc OT).
     * Không tính holiday cuối tuần ở đây — phân ca đã quyết định NV có làm việc ngày đó không.
     */
    BigDecimal tinhSoNgayNghi(UUID nhanVienId, LocalDate tuNgay, LocalDate denNgay) {
        List<PhanCa> caList = phanCaRepo
                .findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(nhanVienId, tuNgay, denNgay);
        long count = caList.stream()
                .map(PhanCa::getNgayApDung)
                .filter(d -> !d.isBefore(tuNgay) && !d.isAfter(denNgay))
                .distinct()
                .count();
        return new BigDecimal(count).setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal safeConLai(com.company.hrm.attendance.dto.QuyPhepNamResponse r) {
        if (r.getSoNgayConLai() != null) return r.getSoNgayConLai();
        if (r.getSoNgayDuocHuong() != null && r.getSoNgayDaDung() != null) {
            return r.getSoNgayDuocHuong().subtract(r.getSoNgayDaDung());
        }
        return null;
    }

    private NghiPhep requireById(UUID id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("NGHI_PHEP_NOT_FOUND", "Không tìm thấy đơn nghỉ phép"));
    }

    private void appendGhiChu(NghiPhep e, String ghiChu, boolean approve, int cap) {
        if (ghiChu == null || ghiChu.isBlank()) return;
        String prefix = approve ? "[Duyệt cấp " + cap + "]" : "[Từ chối cấp " + cap + "]";
        String old = e.getGhiChuDuyet();
        e.setGhiChuDuyet(old == null || old.isBlank() ? prefix + " " + ghiChu : old + "\n" + prefix + " " + ghiChu);
    }
}