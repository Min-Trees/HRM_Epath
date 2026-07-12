package com.company.hrm.hr.offboarding.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.offboarding.dto.SeveranceCalcDto;
import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.SeveranceCalc;
import com.company.hrm.hr.offboarding.repository.OffboardingCaseRepository;
import com.company.hrm.hr.offboarding.repository.SeveranceCalcRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * T14 - Service tinh tro cap thoi viec (severance) theo BLLD 2019, Dieu 44-46.
 *
 * <p>Cong thuc:
 * <pre>
 *   Thoi gian lam viec (thang) = (ngay_nghi_viec_cuoi - ngay_vao_lam) / 30.4375
 *   So nam tham nien = round(thang / 12, 2)
 *   So tien = so_nam * he_so * luong_binh_quan_6_thang
 * </pre>
 *
 * <p>He so:
 * <ul>
 *   <li>0.5 - NGHI_VIEC_TU_NGUYEN, HET_HAN_HDLD, THOI_VIEC, KHAC (BLLD D46)</li>
 *   <li>1.0 - SA_THAI (cong them 1 thang luong theo BLLD D41)</li>
 *   <li>0   - NGHI_HUU (khong co, da huong luong huu)</li>
 * </ul>
 *
 * <p>Tham nien duoi 12 thang -> KHONG duoc tro cap thoi viec (BLLD D46).
 */
@Service
public class SeveranceCalculatorService {

    private static final BigDecimal HS_NGHI_HUU = BigDecimal.ZERO;
    private static final BigDecimal HS_SA_THAI = new BigDecimal("1.0");
    private static final BigDecimal HS_TU_NGUYEN = new BigDecimal("0.5");
    private static final int MIN_THAM_NIEN_THANG = 12;

    private final SeveranceCalcRepository severanceRepo;
    private final OffboardingCaseRepository caseRepo;
    private final NhanVienRepository nhanVienRepo;

    public SeveranceCalculatorService(SeveranceCalcRepository severanceRepo,
                                      OffboardingCaseRepository caseRepo,
                                      NhanVienRepository nhanVienRepo) {
        this.severanceRepo = severanceRepo;
        this.caseRepo = caseRepo;
        this.nhanVienRepo = nhanVienRepo;
    }

    /** Preview tinh toan (khong luu). */
    @Transactional(readOnly = true)
    public SeveranceCalcDto preview(UUID nhanVienId, LocalDate ngayNghiViec, BigDecimal luongBinhQuan6Thang,
                                    LyDoNghiViec lyDo) {
        return calculate(nhanVienId, ngayNghiViec, luongBinhQuan6Thang, lyDo, null);
    }

    /** Tinh va luu vao DB (gan voi offboarding case). */
    @Transactional
    public SeveranceCalcDto calculateAndPersist(UUID caseId, BigDecimal luongBinhQuan6Thang, UUID nguoiTinhId) {
        OffboardingCase c = caseRepo.findById(caseId)
                .orElseThrow(() -> new BusinessException("OFFBOARDING_NOT_FOUND", "Khong tim thay ho so"));
        SeveranceCalcDto dto = calculate(c.getNhanVienId(), c.getNgayNghiViecCuoi(), luongBinhQuan6Thang,
                c.getLyDo(), c.getCaseId());
        dto.setNguoiTinhId(nguoiTinhId);

        SeveranceCalc entity = new SeveranceCalc();
        entity.setCaseId(c.getCaseId());
        entity.setNhanVienId(dto.getNhanVienId());
        entity.setThoiGianLamViecThang(dto.getThoiGianLamViecThang());
        entity.setSoNamThamNien(dto.getSoNamThamNien());
        entity.setLuongThoiViecBinhQuan(dto.getLuongThoiViecBinhQuan());
        entity.setHeSo(dto.getHeSo());
        entity.setSoTienTroCap(dto.getSoTienTroCap());
        entity.setGhiChu(dto.getGhiChu());
        entity.setNguoiTinhId(dto.getNguoiTinhId());
        entity.setNgayTinh(LocalDateTime.now());

        severanceRepo.findByCaseId(c.getCaseId()).ifPresentOrElse(
                existing -> {
                    entity.setSeveranceId(existing.getSeveranceId());
                    entity.setCreatedAt(existing.getCreatedAt());
                },
                () -> entity.setCreatedAt(LocalDateTime.now()));

        SeveranceCalc saved = severanceRepo.save(entity);
        dto.setSeveranceId(saved.getSeveranceId());
        dto.setNgayTinh(saved.getNgayTinh());
        return dto;
    }

    @Transactional(readOnly = true)
    public SeveranceCalcDto getByCase(UUID caseId) {
        SeveranceCalc sc = severanceRepo.findByCaseId(caseId).orElse(null);
        if (sc == null) return null;
        NhanVien nv = nhanVienRepo.findById(sc.getNhanVienId()).orElse(null);
        return toDto(sc, nv);
    }

    private SeveranceCalcDto calculate(UUID nhanVienId, LocalDate ngayNghiViecCuoi,
                                       BigDecimal luongBinhQuan6Thang, LyDoNghiViec lyDo,
                                       UUID caseId) {
        NhanVien nv = nhanVienRepo.findById(nhanVienId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Khong tim thay nhan vien"));

        if (luongBinhQuan6Thang == null || luongBinhQuan6Thang.signum() < 0) {
            throw new BusinessException("INVALID_AVG_SALARY", "Luong binh quan khong hop le");
        }

        long daysBetween = ChronoUnit.DAYS.between(nv.getNgayVaoLam(), ngayNghiViecCuoi);
        int monthsWorked = (int) Math.floor(daysBetween / 30.4375);

        BigDecimal heSo = switch (lyDo) {
            case NGHI_HUU -> HS_NGHI_HUU;
            case SA_THAI -> HS_SA_THAI;
            default -> HS_TU_NGUYEN;
        };

        BigDecimal soTien = BigDecimal.ZERO;
        String ghiChu = "";
        if (monthsWorked < MIN_THAM_NIEN_THANG) {
            ghiChu = "Tham nien duoi 12 thang, khong duoc huong tro cap thoi viec (BLLD D46)";
            heSo = BigDecimal.ZERO;
        } else {
            BigDecimal soNam = BigDecimal.valueOf(monthsWorked)
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            soTien = soNam.multiply(heSo).multiply(luongBinhQuan6Thang)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        SeveranceCalcDto dto = new SeveranceCalcDto();
        dto.setCaseId(caseId);
        dto.setNhanVienId(nhanVienId);
        dto.setThoiGianLamViecThang(monthsWorked);
        dto.setSoNamThamNien(BigDecimal.valueOf(monthsWorked)
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
        dto.setLuongThoiViecBinhQuan(luongBinhQuan6Thang);
        dto.setHeSo(heSo);
        dto.setSoTienTroCap(soTien);
        dto.setGhiChu(ghiChu);
        dto.setNgayTinh(LocalDateTime.now());
        dto.setHoTen(nv.getHoTen());
        dto.setMaNv(nv.getMaNv());
        dto.setNgayVaoLam(nv.getNgayVaoLam());
        dto.setNgayNghiViecCuoi(ngayNghiViecCuoi);
        return dto;
    }

    private SeveranceCalcDto toDto(SeveranceCalc sc, NhanVien nv) {
        SeveranceCalcDto d = new SeveranceCalcDto();
        d.setSeveranceId(sc.getSeveranceId());
        d.setCaseId(sc.getCaseId());
        d.setNhanVienId(sc.getNhanVienId());
        d.setThoiGianLamViecThang(sc.getThoiGianLamViecThang());
        d.setSoNamThamNien(sc.getSoNamThamNien());
        d.setLuongThoiViecBinhQuan(sc.getLuongThoiViecBinhQuan());
        d.setHeSo(sc.getHeSo());
        d.setSoTienTroCap(sc.getSoTienTroCap());
        d.setGhiChu(sc.getGhiChu());
        d.setNguoiTinhId(sc.getNguoiTinhId());
        d.setNgayTinh(sc.getNgayTinh());
        if (nv != null) {
            d.setHoTen(nv.getHoTen());
            d.setMaNv(nv.getMaNv());
            d.setNgayVaoLam(nv.getNgayVaoLam());
        }
        return d;
    }
}
