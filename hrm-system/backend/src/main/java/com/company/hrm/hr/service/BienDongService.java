package com.company.hrm.hr.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.hr.dto.BienDongRequest;
import com.company.hrm.hr.dto.BienDongResponse;
import com.company.hrm.hr.dto.BienDongTimelineItem;
import com.company.hrm.hr.entity.BienDongNhanSu;
import com.company.hrm.hr.entity.BienDongNhanSu.LoaiBienDong;
import com.company.hrm.hr.entity.BienDongNhanSu.TrangThaiNvSau;
import com.company.hrm.hr.entity.HopDongLaoDong;
import com.company.hrm.hr.entity.HopDongLaoDong.TrangThaiHopDong;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import com.company.hrm.hr.event.EmployeeSalaryChangedEvent;
import com.company.hrm.hr.event.EmployeeStatusChangedEvent;
import com.company.hrm.hr.event.EmployeeTransferredEvent;
import com.company.hrm.hr.repository.BienDongNhanSuRepository;
import com.company.hrm.hr.repository.HopDongLaoDongRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Biến động nhân sự — append-only.
 *
 * <p>Service này là <b>NƠI DUY NHẤT</b> được phép thay đổi các trường phái sinh
 * trên {@link NhanVien} ({@code trang_thai}, {@code phong_ban_id},
 * {@code ngach_bac_id}, {@code quan_ly_truc_tiep_id}) và cập nhật lương trên HĐ đang hiệu lực.
 * {@link NhanVienService#updateInfo} đã chặn đổi các trường này ở tầng API.
 *
 * <p>Quy tắc:
 * <ul>
 *   <li>Bảng {@code hr.bien_dong_nhan_su} chỉ INSERT — repository không có method update/delete.</li>
 *   <li>Trước khi insert: snapshot các trường "trước" từ {@link NhanVien} hiện tại.</li>
 *   <li>Sau insert: cập nhật các trường phái sinh trên NV và (nếu có) {@code luong_thoa_thuan} trên HĐ đang hiệu lực.</li>
 *   <li>Phát event để module khác (BHXH, payroll, chấm công) đồng bộ.</li>
 * </ul>
 */
@Service
public class BienDongService {

    /** Ma trận chuyển trạng thái hợp lệ: từ {@code TrangThaiNv} -> tập {@code TrangThaiNv} được phép đi tới. */
    private static final Map<TrangThaiNv, Set<TrangThaiNv>> ALLOWED_TRANSITIONS;

    static {
        ALLOWED_TRANSITIONS = new EnumMap<>(TrangThaiNv.class);
        ALLOWED_TRANSITIONS.put(TrangThaiNv.UNG_VIEN,
                EnumSet.of(TrangThaiNv.THU_VIEC, TrangThaiNv.CHINH_THUC, TrangThaiNv.DA_NGHI_VIEC));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.THU_VIEC,
                EnumSet.of(TrangThaiNv.CHINH_THUC, TrangThaiNv.UNG_VIEN, TrangThaiNv.DA_NGHI_VIEC));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.CHINH_THUC,
                EnumSet.of(TrangThaiNv.TAM_HOAN_HDLD, TrangThaiNv.DA_NGHI_VIEC, TrangThaiNv.DA_NGHI_HUU));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.TAM_HOAN_HDLD,
                EnumSet.of(TrangThaiNv.CHINH_THUC, TrangThaiNv.DA_NGHI_VIEC, TrangThaiNv.DA_NGHI_HUU));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.DA_NGHI_VIEC, EnumSet.of(TrangThaiNv.LUU_TRU));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.DA_NGHI_HUU, EnumSet.of(TrangThaiNv.LUU_TRU));
        ALLOWED_TRANSITIONS.put(TrangThaiNv.LUU_TRU, EnumSet.noneOf(TrangThaiNv.class));
    }

    private final BienDongNhanSuRepository repo;
    private final NhanVienRepository nvRepo;
    private final HopDongLaoDongRepository hdRepo;
    private final ApplicationEventPublisher events;

    public BienDongService(BienDongNhanSuRepository repo,
                           NhanVienRepository nvRepo,
                           HopDongLaoDongRepository hdRepo,
                           ApplicationEventPublisher events) {
        this.repo = repo;
        this.nvRepo = nvRepo;
        this.hdRepo = hdRepo;
        this.events = events;
    }

    /**
     * Ghi biến động + cập nhật trường phái sinh trên NV + (nếu đổi lương) cập nhật HĐ hiệu lực.
     */
    @Transactional
    public BienDongResponse create(BienDongRequest req) {
        NhanVien nv = nvRepo.findById(req.getNhanVienId()).orElseThrow(() ->
                new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));

        validate(req, nv);

        TrangThaiNv oldStatus = nv.getTrangThai();
        TrangThaiNv newStatus = req.getTrangThaiNvSau();

        BienDongNhanSu bd = new BienDongNhanSu();
        bd.setNhanVienId(nv.getNhanVienId());
        bd.setLoaiBienDong(req.getLoaiBienDong());
        bd.setSoQuyetDinh(req.getSoQuyetDinh());
        bd.setNgayQuyetDinh(req.getNgayQuyetDinh());
        bd.setNgayHieuLuc(req.getNgayHieuLuc());

        // Snapshot "trước"
        bd.setPhongBanTruocId(nv.getPhongBanId());
        bd.setLuongTruoc(snapshotCurrentSalary(nv.getNhanVienId()));

        // "Sau"
        if (req.getPhongBanSauId() != null) {
            bd.setPhongBanSauId(req.getPhongBanSauId());
        } else if (req.getLoaiBienDong() == LoaiBienDong.DIEU_CHUYEN
                || req.getLoaiBienDong() == LoaiBienDong.BO_NHIEM) {
            throw new BusinessException("BIEN_DONG_PB_REQUIRED",
                    "Biến động " + req.getLoaiBienDong() + " bắt buộc phải có phong_ban_sau_id");
        }
        bd.setChucDanhSau(req.getChucDanhSau());
        bd.setLuongSau(req.getLuongSau());
        bd.setTrangThaiNvSau(TrangThaiNvSau.valueOf(newStatus.name()));
        bd.setLyDo(req.getLyDo());
        bd.setFileQuyetDinhUrl(req.getFileQuyetDinhUrl());

        BienDongNhanSu saved = repo.save(bd);

        // Cập nhật trường phái sinh trên NV
        nv.setTrangThai(newStatus);
        if (req.getPhongBanSauId() != null) {
            nv.setPhongBanId(req.getPhongBanSauId());
        }
        if (req.getNgachBacSauId() != null) {
            nv.setNgachBacId(req.getNgachBacSauId());
        }
        if (req.getQuanLyTrucTiepSauId() != null) {
            nv.setQuanLyTrucTiepId(req.getQuanLyTrucTiepSauId());
        }
        nvRepo.save(nv);

        // Cập nhật lương trên HĐ đang hiệu lực (nếu có)
        boolean salaryUpdated = false;
        if (req.getLuongSau() != null) {
            HopDongLaoDong active = hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                    nv.getNhanVienId(),
                    TrangThaiHopDong.HIEU_LUC,
                    List.of(HopDongLaoDong.LoaiHopDong.THU_VIEC,
                            HopDongLaoDong.LoaiHopDong.XAC_DINH_THOI_HAN,
                            HopDongLaoDong.LoaiHopDong.KHONG_XAC_DINH_THOI_HAN)
            ).orElseThrow(() -> new BusinessException("NO_ACTIVE_CONTRACT_TO_ADJUST_SALARY",
                    "Không có hợp đồng chính hiệu lực để cập nhật lương; tạo phụ lục / hợp đồng mới trước"));
            active.setMucLuongThoaThuan(req.getLuongSau());
            hdRepo.save(active);
            salaryUpdated = true;
        }

        // Phát event
        events.publishEvent(new EmployeeStatusChangedEvent(
                nv.getNhanVienId(), oldStatus, newStatus,
                req.getNgayHieuLuc(), req.getLoaiBienDong().name()));
        if (req.getPhongBanSauId() != null && !req.getPhongBanSauId().equals(bd.getPhongBanTruocId())) {
            events.publishEvent(new EmployeeTransferredEvent(
                    nv.getNhanVienId(), bd.getPhongBanTruocId(), req.getPhongBanSauId(),
                    req.getNgayHieuLuc()));
        }
        if (salaryUpdated) {
            events.publishEvent(new EmployeeSalaryChangedEvent(
                    nv.getNhanVienId(), bd.getLuongTruoc(), req.getLuongSau(),
                    req.getNgayHieuLuc()));
        }
        return BienDongResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public BienDongResponse get(UUID id) {
        return repo.findById(id)
                .map(BienDongResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("BIEN_DONG_NOT_FOUND",
                        "Không tìm thấy biến động"));
    }

    @Transactional(readOnly = true)
    public List<BienDongResponse> listByNhanVien(UUID nhanVienId) {
        return repo.findByNhanVienIdOrderByNgayHieuLucDesc(nhanVienId).stream()
                .map(BienDongResponse::from)
                .toList();
    }

    /**
     * Suy ra trạng thái NV tại {@code date}: lấy biến động mới nhất có {@code ngay_hieu_luc <= date}.
     * Nếu không có biến động nào trước/đúng date → trả {@link TrangThaiNv#UNG_VIEN} (trạng thái khởi tạo).
     */
    @Transactional(readOnly = true)
    public BienDongTimelineItem trangThaiTaiNgay(UUID nhanVienId, LocalDate date) {
        Optional<BienDongNhanSu> latest = repo
                .findFirstByNhanVienIdAndNgayHieuLucLessThanEqualOrderByNgayHieuLucDesc(nhanVienId, date);
        if (latest.isEmpty()) {
            return new BienDongTimelineItem(date, TrangThaiNv.UNG_VIEN, "Chưa có biến động trước ngày này");
        }
        BienDongNhanSu bd = latest.get();
        return new BienDongTimelineItem(
                bd.getNgayHieuLuc(),
                TrangThaiNv.valueOf(bd.getTrangThaiNvSau().name()),
                bd.getLyDo());
    }

    /**
     * Validate biến động:
     * <ul>
     *   <li>{@code ngay_hieu_luc >= ngay_quyet_dinh}</li>
     *   <li>{@code ngay_hieu_luc >= ngay_vao_lam} của NV</li>
     *   <li>{@code so_quyet_dinh} duy nhất trong cùng NV</li>
     *   <li>Chuyển trạng thái phải hợp lệ theo ma trận {@link #ALLOWED_TRANSITIONS}</li>
     *   <li>Biến động đổi lương → phải có {@code luong_sau > 0}</li>
     * </ul>
     */
    private void validate(BienDongRequest req, NhanVien nv) {
        if (req.getNgayHieuLuc().isBefore(req.getNgayQuyetDinh())) {
            throw new BusinessException("BIEN_DONG_NGAY_HIEU_LUC_INVALID",
                    "Ngày hiệu lực phải >= ngày quyết định");
        }
        if (req.getNgayHieuLuc().isBefore(nv.getNgayVaoLam())) {
            throw new BusinessException("BIEN_DONG_BEFORE_JOIN_DATE",
                    "Ngày hiệu lực phải >= ngày vào làm của nhân viên");
        }
        if (repo.existsByNhanVienIdAndSoQuyetDinh(req.getNhanVienId(), req.getSoQuyetDinh())) {
            throw new BusinessException("BIEN_DONG_SO_QUYET_DINH_DUPLICATE",
                    "Số quyết định '" + req.getSoQuyetDinh() + "' đã tồn tại cho nhân viên này");
        }
        TrangThaiNv current = nv.getTrangThai();
        TrangThaiNv target = req.getTrangThaiNvSau();
        if (current == target) {
            // Cho phép DIEU_CHUYEN/DIEU_CHINH_LUONG không đổi trạng thái
            if (req.getLoaiBienDong() != LoaiBienDong.DIEU_CHUYEN
                    && req.getLoaiBienDong() != LoaiBienDong.DIEU_CHINH_LUONG
                    && req.getLoaiBienDong() != LoaiBienDong.BO_NHIEM
                    && req.getLoaiBienDong() != LoaiBienDong.KY_LUAT) {
                throw new BusinessException("BIEN_DONG_STATUS_UNCHANGED",
                        "Biến động loại " + req.getLoaiBienDong()
                                + " yêu cầu trạng thái mới phải khác trạng thái hiện tại");
            }
        } else {
            Set<TrangThaiNv> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(TrangThaiNv.class));
            if (!allowed.contains(target)) {
                throw new BusinessException("INVALID_STATUS_TRANSITION",
                        "Không thể chuyển trạng thái từ " + current + " sang " + target);
            }
        }
        if (req.getLuongSau() != null && req.getLuongSau().signum() <= 0) {
            throw new BusinessException("BIEN_DONG_LUONG_INVALID",
                    "Mức lương sau phải > 0");
        }
        if (req.getLoaiBienDong() == LoaiBienDong.DIEU_CHINH_LUONG && req.getLuongSau() == null) {
            throw new BusinessException("BIEN_DONG_LUONG_REQUIRED",
                    "Biến động DIEU_CHINH_LUONG bắt buộc phải có luong_sau");
        }
    }

    private java.math.BigDecimal snapshotCurrentSalary(UUID nhanVienId) {
        return hdRepo.findFirstByNhanVienIdAndTrangThaiAndLoaiHopDongIn(
                nhanVienId,
                TrangThaiHopDong.HIEU_LUC,
                List.of(HopDongLaoDong.LoaiHopDong.THU_VIEC,
                        HopDongLaoDong.LoaiHopDong.XAC_DINH_THOI_HAN,
                        HopDongLaoDong.LoaiHopDong.KHONG_XAC_DINH_THOI_HAN)
        ).map(HopDongLaoDong::getMucLuongThoaThuan).orElse(null);
    }
}
