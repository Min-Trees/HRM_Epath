package com.company.hrm.attendance.service;

import com.company.hrm.attendance.dto.DangKyOtMonthlySummary;
import com.company.hrm.attendance.dto.DangKyOtRequest;
import com.company.hrm.attendance.dto.DangKyOtResponse;
import com.company.hrm.attendance.entity.DangKyOt;
import com.company.hrm.attendance.entity.HeSoOt;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.repository.DangKyOtRepository;
import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ đăng ký tăng ca (T10) với quy trình duyệt 2 cấp:
 * {@code CHO_DUYET → DUYET_CAP_1 (MANAGER) → DA_DUYET (HR)}, hoặc {@code TU_CHOI} tại bất kỳ cấp.
 *
 * <p>{@code heSoOt} do client (HR/Manager) truyền — backend không validate với ngày
 * (vì có thể OT ngày lễ xin 150% vì lý do đặc biệt).
 *
 * <p>OT chỉ được tính lương khi {@code DA_DUYET}. {@link #monthlyApprovedOT} cung cấp
 * cho T11 tổng hợp theo hệ số (150/200/300).
 */
@Service
public class DangKyOtService {

    private static final Logger log = LoggerFactory.getLogger(DangKyOtService.class);
    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

    private final DangKyOtRepository repo;
    private final NhanVienRepository nvRepo;

    public DangKyOtService(DangKyOtRepository repo, NhanVienRepository nvRepo) {
        this.repo = repo;
        this.nvRepo = nvRepo;
    }

    // ---------------- create ----------------

    @Transactional
    public DangKyOtResponse create(DangKyOtRequest req) {
        validate(req);
        if (!nvRepo.existsById(req.getNhanVienId())) {
            throw new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }

        DangKyOt e = new DangKyOt();
        e.setNhanVienId(req.getNhanVienId());
        e.setNgayLamOt(req.getNgayLamOt());
        e.setGioBatDau(req.getGioBatDau());
        e.setGioKetThuc(req.getGioKetThuc());
        e.setSoGioOt(tinhSoGioOt(req.getGioBatDau(), req.getGioKetThuc()));
        e.setHeSoOt(req.getHeSoOt());
        e.setLamDem(Boolean.TRUE.equals(req.getLamDem()));
        e.setLyDo(req.getLyDo());
        e.setTrangThai(TrangThaiDon.CHO_DUYET);

        DangKyOt saved = repo.save(e);
        log.info("Tạo đơn OT NV {} ngày {} = {} giờ, hệ số {}, làm đêm: {}",
                req.getNhanVienId(), req.getNgayLamOt(),
                saved.getSoGioOt(), saved.getHeSoOt(), saved.isLamDem());
        return DangKyOtResponse.from(saved);
    }

    // ---------------- approve 2 cấp ----------------

    @Transactional
    public DangKyOtResponse approveCap1(UUID id, UUID approverId, boolean approve, String ghiChu) {
        DangKyOt e = requireById(id);
        if (e.getTrangThai() != TrangThaiDon.CHO_DUYET) {
            throw new BusinessException("OT_INVALID_STATE",
                    "Đơn OT phải ở trạng thái CHO_DUYET để duyệt cấp 1 (hiện tại: "
                            + e.getTrangThai() + ")");
        }
        e.setDuyetCap1Boi(approverId);
        e.setDuyetCap1Luc(OffsetDateTime.now(DEFAULT_OFFSET));
        e.setTrangThai(approve ? TrangThaiDon.DUYET_CAP_1 : TrangThaiDon.TU_CHOI);
        appendGhiChu(e, ghiChu, approve, 1);
        log.info("NV {} duyệt cấp 1 OT {} → {}", approverId, id, e.getTrangThai());
        return DangKyOtResponse.from(repo.save(e));
    }

    @Transactional
    public DangKyOtResponse approveCap2(UUID id, UUID approverId, boolean approve, String ghiChu) {
        DangKyOt e = requireById(id);
        if (e.getTrangThai() != TrangThaiDon.DUYET_CAP_1) {
            throw new BusinessException("OT_INVALID_STATE",
                    "Đơn OT phải ở trạng thái DUYET_CAP_1 để duyệt cấp 2 (hiện tại: "
                            + e.getTrangThai() + ")");
        }
        if (approverId != null && approverId.equals(e.getDuyetCap1Boi())) {
            throw new BusinessException("APPROVER_DUPLICATE",
                    "Người duyệt cấp 2 không được trùng với người duyệt cấp 1");
        }
        e.setDuyetCap2Boi(approverId);
        e.setDuyetCap2Luc(OffsetDateTime.now(DEFAULT_OFFSET));
        e.setTrangThai(approve ? TrangThaiDon.DA_DUYET : TrangThaiDon.TU_CHOI);
        appendGhiChu(e, ghiChu, approve, 2);
        log.info("NV {} duyệt cấp 2 OT {} → {}", approverId, id, e.getTrangThai());
        return DangKyOtResponse.from(repo.save(e));
    }

    // ---------------- cancel ----------------

    /**
     * Hủy đơn OT. Đơn đã DA_DUYET có thể hủy nhưng không hoàn gì (HR audit).
     */
    @Transactional
    public DangKyOtResponse cancel(UUID id) {
        DangKyOt e = requireById(id);
        if (e.getTrangThai() == TrangThaiDon.HUY) {
            throw new BusinessException("OT_DA_HUY", "Đơn OT đã hủy");
        }
        e.setTrangThai(TrangThaiDon.HUY);
        log.info("Hủy đơn OT {} (trạng thái cũ: {})", id, e.getTrangThai());
        return DangKyOtResponse.from(repo.save(e));
    }

    // ---------------- queries ----------------

    @Transactional(readOnly = true)
    public DangKyOtResponse get(UUID id) {
        return DangKyOtResponse.from(requireById(id));
    }

    @Transactional(readOnly = true)
    public List<DangKyOtResponse> list(UUID employeeId, List<TrangThaiDon> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return repo.findAll().stream()
                    .filter(o -> employeeId == null || o.getNhanVienId().equals(employeeId))
                    .map(DangKyOtResponse::from)
                    .toList();
        }
        return repo.findByNhanVienIdAndTrangThaiInOrderByNgayLamOtDesc(employeeId, statuses)
                .stream().map(DangKyOtResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<DangKyOtResponse> listByMonth(UUID employeeId, int thang, int nam) {
        LocalDate from = LocalDate.of(nam, thang, 1);
        LocalDate to = from.plusMonths(1).minusDays(1);
        return repo.findByNhanVienIdAndNgayLamOtBetweenOrderByNgayLamOtAsc(employeeId, from, to)
                .stream().map(DangKyOtResponse::from).toList();
    }

    /**
     * Tổng hợp OT đã duyệt theo NV và tháng — cho T11.
     */
    @Transactional(readOnly = true)
    public DangKyOtMonthlySummary monthlyApprovedOT(UUID employeeId, int thang, int nam) {
        validateMonth(thang, nam);
        LocalDate from = LocalDate.of(nam, thang, 1);
        LocalDate to = from.plusMonths(1).minusDays(1);
        BigDecimal ot150 = repo.sumApprovedByMonthAndHeSo(employeeId, TrangThaiDon.DA_DUYET,
                from, to, HeSoOt.NGAY_THUONG_150);
        BigDecimal ot200 = repo.sumApprovedByMonthAndHeSo(employeeId, TrangThaiDon.DA_DUYET,
                from, to, HeSoOt.NGAY_NGHI_TUAN_200);
        BigDecimal ot300 = repo.sumApprovedByMonthAndHeSo(employeeId, TrangThaiDon.DA_DUYET,
                from, to, HeSoOt.NGAY_LE_300);
        return new DangKyOtMonthlySummary(employeeId, thang, nam,
                nz(ot150), nz(ot200), nz(ot300));
    }

    // ---------------- helpers ----------------

    private void validate(DangKyOtRequest req) {
        if (!req.getGioKetThuc().isAfter(req.getGioBatDau())) {
            throw new BusinessException("OT_GIO_INVALID", "gioKetThuc phải sau gioBatDau");
        }
        if (req.getGioBatDau().toLocalDate().isAfter(req.getNgayLamOt())) {
            throw new BusinessException("OT_NGAY_INVALID",
                    "gioBatDau phải cùng ngày hoặc sau ngayLamOt");
        }
    }

    static BigDecimal tinhSoGioOt(OffsetDateTime gioBatDau, OffsetDateTime gioKetThuc) {
        long minutes = ChronoUnit.MINUTES.between(gioBatDau.toInstant(), gioKetThuc.toInstant());
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private void validateMonth(int thang, int nam) {
        if (thang < 1 || thang > 12) {
            throw new BusinessException("MONTH_INVALID", "Tháng không hợp lệ: " + thang);
        }
        if (nam < 1970 || nam > 9999) {
            throw new BusinessException("YEAR_INVALID", "Năm không hợp lệ: " + nam);
        }
    }

    private DangKyOt requireById(UUID id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("OT_NOT_FOUND", "Không tìm thấy đơn OT"));
    }

    private void appendGhiChu(DangKyOt e, String ghiChu, boolean approve, int cap) {
        if (ghiChu == null || ghiChu.isBlank()) return;
        String prefix = approve ? "[Duyệt cấp " + cap + "]" : "[Từ chối cấp " + cap + "]";
        String old = e.getGhiChuDuyet();
        e.setGhiChuDuyet(old == null || old.isBlank() ? prefix + " " + ghiChu : old + "\n" + prefix + " " + ghiChu);
    }
}