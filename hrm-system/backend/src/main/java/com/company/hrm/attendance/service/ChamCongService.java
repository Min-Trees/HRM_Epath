package com.company.hrm.attendance.service;

import com.company.hrm.attendance.config.AttendanceProperties;
import com.company.hrm.attendance.dto.BatchTimeLogResult;
import com.company.hrm.attendance.dto.MonthlySummary;
import com.company.hrm.attendance.dto.TimeLogRequest;
import com.company.hrm.attendance.dto.TimeLogResponse;
import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.ChamCongChiTiet;
import com.company.hrm.attendance.entity.LoaiNgoaiLe;
import com.company.hrm.attendance.entity.NguonChamCong;
import com.company.hrm.attendance.entity.PhanCa;
import com.company.hrm.attendance.entity.TrangThaiDon;
import com.company.hrm.attendance.repository.ChamCongChiTietRepository;
import com.company.hrm.attendance.repository.PhanCaRepository;
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
 * Nghiệp vụ thu thập chấm công và xử lý ngoại lệ (T09).
 *
 * <h3>Quy tắc phát hiện ngoại lệ (theo thứ tự ưu tiên):</h3>
 * <ol>
 *   <li>Không có phân ca cho {@code (nhan_vien_id, ngay_cham_cong)} → {@link LoaiNgoaiLe#LAM_NGOAI_CA},
 *       {@code can_giai_trinh=true}.</li>
 *   <li>Có phân ca nhưng thiếu {@code gioVao} hoặc {@code gioRa} → {@link LoaiNgoaiLe#THIEU_CONG},
 *       {@code can_giai_trinh=true}.</li>
 *   <li>{@code gioVao} muộn hơn giờ bắt đầu ca quá ngưỡng ({@link AttendanceProperties#getNguongDiTrePhut()})
 *       → {@link LoaiNgoaiLe#DI_TRE}, {@code can_giai_trinh=true}.</li>
 *   <li>{@code gioRa} sớm hơn giờ kết thúc ca quá ngưỡng ({@link AttendanceProperties#getNguongVeSomPhut()})
 *       → {@link LoaiNgoaiLe#VE_SOM}, {@code can_giai_trinh=true}.</li>
 *   <li>Còn lại → {@link LoaiNgoaiLe#KHONG_NGOAI_LE}, {@code can_giai_trinh=false}.</li>
 * </ol>
 *
 * <p>Nguồn {@link NguonChamCong#THU_CONG}: bản ghi tạo với {@code giai_trinh_trang_thai = CHO_DUYET}
 * (cần HR/MANAGER duyệt mới tính hợp lệ). Các nguồn khác: không yêu cầu duyệt.
 *
 * <p>Lưu ý về overnight shift ({@code qua_ngay=true}): giờ kết thúc ca được tính ở ngày kế tiếp
 * khi so với {@code gio_ra}.
 */
@Service
public class ChamCongService {

    private static final Logger log = LoggerFactory.getLogger(ChamCongService.class);
    private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

    private final ChamCongChiTietRepository repo;
    private final PhanCaRepository phanCaRepo;
    private final NhanVienRepository nvRepo;
    private final PhanCaService phanCaService;
    private final AttendanceProperties props;

    public ChamCongService(ChamCongChiTietRepository repo,
                           PhanCaRepository phanCaRepo,
                           NhanVienRepository nvRepo,
                           PhanCaService phanCaService,
                           AttendanceProperties props) {
        this.repo = repo;
        this.phanCaRepo = phanCaRepo;
        this.nvRepo = nvRepo;
        this.phanCaService = phanCaService;
        this.props = props;
    }

    // ---------------- record ----------------

    /**
     * Ghi 1 bản ghi chấm công. Nếu {@code (nhan_vien_id, ngay_cham_cong)} đã tồn tại → lỗi
     * (batch dùng cơ chế skip riêng).
     */
    @Transactional
    public TimeLogResponse record(TimeLogRequest req) {
        validate(req);
        if (repo.existsByNhanVienIdAndNgayChamCong(req.getNhanVienId(), req.getNgayChamCong())) {
            throw new BusinessException("CHAM_CONG_DUPLICATE",
                    "Đã có chấm công cho nhân viên " + req.getNhanVienId()
                            + " ngày " + req.getNgayChamCong());
        }
        ChamCongChiTiet saved = buildAndSave(req);
        return TimeLogResponse.from(saved);
    }

    /**
     * Đồng bộ lô. Bỏ qua các bản ghi trùng ngày (giữ bản ghi đầu tiên).
     * Validation lỗi (thiếu NV, sai ca…) cũng tính là skipped để không chặn cả lô.
     */
    @Transactional
    public BatchTimeLogResult recordBatch(List<TimeLogRequest> records) {
        int total = records == null ? 0 : records.size();
        if (total == 0) return new BatchTimeLogResult(0, 0, 0);
        int created = 0, skipped = 0;
        for (TimeLogRequest r : records) {
            try {
                record(r);
                created++;
            } catch (BusinessException ex) {
                log.warn("Skip batch record: {}", ex.getMessage());
                skipped++;
            }
        }
        return new BatchTimeLogResult(total, created, skipped);
    }

    // ---------------- queries ----------------

    @Transactional(readOnly = true)
    public TimeLogResponse get(UUID id) {
        return TimeLogResponse.from(requireById(id));
    }

    @Transactional(readOnly = true)
    public List<TimeLogResponse> list(UUID employeeId, LocalDate from, LocalDate to) {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
        return repo.findByNhanVienIdAndNgayChamCongBetweenOrderByNgayChamCongAsc(employeeId, from, to)
                .stream().map(TimeLogResponse::from).toList();
    }

    /**
     * Danh sách ngoại lệ (loại trừ {@link LoaiNgoaiLe#KHONG_NGOAI_LE}).
     * Lọc thêm theo {@code status} nếu cần.
     */
    @Transactional(readOnly = true)
    public List<TimeLogResponse> listExceptions(LocalDate from, LocalDate to, TrangThaiDon status) {
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to == null) to = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1);
        List<ChamCongChiTiet> all = repo
                .findByLoaiNgoaiLeNotAndNgayChamCongBetweenOrderByNgayChamCongAsc(
                        LoaiNgoaiLe.KHONG_NGOAI_LE, from, to);
        return all.stream()
                .filter(e -> status == null || status == e.getGiaiTrinhTrangThai())
                .map(TimeLogResponse::from)
                .toList();
    }

    /**
     * Tổng hợp công theo NV và tháng — T11 sẽ gọi.
     * {@code soNgayCong} = tổng bản ghi trong tháng (kể cả có ngoại lệ — payroll sẽ tự quyết định);
     * {@code soNgayNgoaiLe} = trong đó bao nhiêu có ngoại lệ; {@code soNgayNghi} = số ngày không có log
     * (công thức = số ngày trong tháng - {@code soNgayCong}).
     */
    @Transactional(readOnly = true)
    public MonthlySummary summary(UUID employeeId, int thang, int nam) {
        if (thang < 1 || thang > 12) {
            throw new BusinessException("MONTH_INVALID", "Tháng không hợp lệ: " + thang);
        }
        if (nam < 1970 || nam > 9999) {
            throw new BusinessException("YEAR_INVALID", "Năm không hợp lệ: " + nam);
        }
        if (!nvRepo.existsById(employeeId)) {
            throw new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }
        LocalDate from = LocalDate.of(nam, thang, 1);
        LocalDate to = from.plusMonths(1).minusDays(1);
        List<ChamCongChiTiet> logs = repo
                .findByNhanVienIdAndNgayChamCongBetweenOrderByNgayChamCongAsc(employeeId, from, to);
        BigDecimal tongGio = logs.stream()
                .map(ChamCongChiTiet::getSoGioCong)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        int soNgayCong = logs.size();
        int soNgayNgoaiLe = (int) logs.stream()
                .filter(e -> e.getLoaiNgoaiLe() != LoaiNgoaiLe.KHONG_NGOAI_LE).count();
        int soNgayNghi = Math.max(0, MonthlySummary.daysInRange(from, to) - soNgayCong);
        return new MonthlySummary(employeeId, thang, nam,
                soNgayCong, soNgayNghi, soNgayNgoaiLe, tongGio,
                logs.stream().map(TimeLogResponse::from).toList());
    }

    // ---------------- explanation / approval ----------------

    /**
     * HR gửi giải trình hộ NV. Yêu cầu {@code can_giai_trinh=true}.
     * Đặt {@code giai_trinh_trang_thai=CHO_DUYET}.
     */
    @Transactional
    public TimeLogResponse submitExplanation(UUID logId, String noiDung) {
        ChamCongChiTiet e = requireById(logId);
        if (!e.isCanGiaiTrinh()) {
            throw new BusinessException("CHAM_CONG_KHONG_CAN_GIAI_TRINH",
                    "Bản ghi chấm công không có ngoại lệ cần giải trình");
        }
        if (e.getGiaiTrinhTrangThai() == TrangThaiDon.DA_DUYET) {
            throw new BusinessException("CHAM_CONG_DA_DUYET",
                    "Bản ghi đã được duyệt, không thể gửi giải trình mới");
        }
        e.setGiaiTrinhNoiDung(noiDung);
        e.setGiaiTrinhTrangThai(TrangThaiDon.CHO_DUYET);
        // reset trạng thái duyệt cũ (nếu có)
        e.setDuyetBoi(null);
        e.setDuyetLuc(null);
        return TimeLogResponse.from(repo.save(e));
    }

    /**
     * MANAGER/HR duyệt hoặc từ chối.
     */
    @Transactional
    public TimeLogResponse approve(UUID logId, boolean approve, UUID approverId, String ghiChu) {
        ChamCongChiTiet e = requireById(logId);
        if (e.getGiaiTrinhTrangThai() != TrangThaiDon.CHO_DUYET
                && e.getGiaiTrinhTrangThai() != TrangThaiDon.TU_CHOI) {
            throw new BusinessException("CHAM_CONG_KHONG_THE_DUYET",
                    "Bản ghi không ở trạng thái chờ duyệt/từ chối (hiện tại: "
                            + e.getGiaiTrinhTrangThai() + ")");
        }
        e.setGiaiTrinhTrangThai(approve ? TrangThaiDon.DA_DUYET : TrangThaiDon.TU_CHOI);
        e.setDuyetBoi(approverId);
        e.setDuyetLuc(OffsetDateTime.now(DEFAULT_OFFSET));
        if (ghiChu != null && !ghiChu.isBlank()) {
            e.setGiaiTrinhNoiDung(appendGhiChu(e.getGiaiTrinhNoiDung(), ghiChu, approve));
        }
        return TimeLogResponse.from(repo.save(e));
    }

    // ---------------- helpers ----------------

    private void validate(TimeLogRequest req) {
        if (!nvRepo.existsById(req.getNhanVienId())) {
            throw new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên");
        }
        if (req.getGioVao() != null && req.getGioRa() != null
                && req.getGioRa().isBefore(req.getGioVao())) {
            throw new BusinessException("CHAM_CONG_GIO_RA_TRUOC_GIO_VAO",
                    "Giờ ra phải >= giờ vào");
        }
        if (req.getNguon() == NguonChamCong.GPS_MOBILE) {
            if (req.getViTriGpsLat() == null || req.getViTriGpsLng() == null) {
                throw new BusinessException("CHAM_CONG_GPS_REQUIRED",
                        "Nguồn GPS_MOBILE yêu cầu tọa độ (lat/lng)");
            }
        }
    }

    private ChamCongChiTiet buildAndSave(TimeLogRequest req) {
        CaLamViec ca = phanCaService.getStandardShift(req.getNhanVienId(), req.getNgayChamCong());
        PhanCa phanCa = phanCaRepo.findByNhanVienIdAndNgayApDung(req.getNhanVienId(), req.getNgayChamCong())
                .orElse(null);

        ChamCongChiTiet e = new ChamCongChiTiet();
        e.setNhanVienId(req.getNhanVienId());
        e.setPhanCaId(phanCa != null ? phanCa.getPhanCaId() : null);
        e.setNgayChamCong(req.getNgayChamCong());
        e.setGioVao(req.getGioVao());
        e.setGioRa(req.getGioRa());
        e.setNguon(req.getNguon());
        e.setViTriGpsLat(req.getViTriGpsLat());
        e.setViTriGpsLng(req.getViTriGpsLng());

        Detection d = detectException(ca, req.getNgayChamCong(), req.getGioVao(), req.getGioRa());
        e.setLoaiNgoaiLe(d.loai);
        e.setCanGiaiTrinh(d.canGiaiTrinh);
        e.setSoGioCong(calcSoGioCong(req.getGioVao(), req.getGioRa()));

        if (req.getNguon() == NguonChamCong.THU_CONG) {
            e.setGiaiTrinhTrangThai(TrangThaiDon.CHO_DUYET);
        }
        return repo.save(e);
    }

    private record Detection(LoaiNgoaiLe loai, boolean canGiaiTrinh) {}

    private Detection detectException(CaLamViec ca, LocalDate ngay,
                                      OffsetDateTime gioVao, OffsetDateTime gioRa) {
        if (ca == null) {
            return new Detection(LoaiNgoaiLe.LAM_NGOAI_CA, true);
        }
        if (gioVao == null || gioRa == null) {
            return new Detection(LoaiNgoaiLe.THIEU_CONG, true);
        }
        OffsetDateTime caStart = OffsetDateTime.of(ngay, ca.getGioBatDau(), DEFAULT_OFFSET);
        OffsetDateTime caEnd = OffsetDateTime.of(ngay, ca.getGioKetThuc(), DEFAULT_OFFSET);

        // Vào trễ
        long diTrePhut = ChronoUnit.MINUTES.between(
                caStart.toInstant(), gioVao.toInstant());
        if (diTrePhut > props.getNguongDiTrePhut()) {
            return new Detection(LoaiNgoaiLe.DI_TRE, true);
        }

        // Về sớm — với ca qua đêm, kết thúc ca sang ngày hôm sau
        if (ca.isQuaNgay()) caEnd = caEnd.plusDays(1);
        long veSomPhut = ChronoUnit.MINUTES.between(
                gioRa.toInstant(), caEnd.toInstant());
        if (veSomPhut > props.getNguongVeSomPhut()) {
            return new Detection(LoaiNgoaiLe.VE_SOM, true);
        }
        return new Detection(LoaiNgoaiLe.KHONG_NGOAI_LE, false);
    }

    private BigDecimal calcSoGioCong(OffsetDateTime gioVao, OffsetDateTime gioRa) {
        if (gioVao == null || gioRa == null) return null;
        long minutes = ChronoUnit.MINUTES.between(gioVao.toInstant(), gioRa.toInstant());
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private ChamCongChiTiet requireById(UUID id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("CHAM_CONG_NOT_FOUND", "Không tìm thấy bản ghi chấm công"));
    }

    private String appendGhiChu(String original, String ghiChuMoi, boolean approve) {
        String prefix = approve ? "[Đã duyệt]" : "[Từ chối]";
        if (original == null || original.isBlank()) return prefix + " " + ghiChuMoi;
        return original + "\n" + prefix + " " + ghiChuMoi;
    }
}