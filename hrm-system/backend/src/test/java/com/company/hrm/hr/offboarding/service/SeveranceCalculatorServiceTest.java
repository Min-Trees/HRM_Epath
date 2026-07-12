package com.company.hrm.hr.offboarding.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.offboarding.dto.SeveranceCalcDto;
import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.repository.OffboardingCaseRepository;
import com.company.hrm.hr.offboarding.repository.SeveranceCalcRepository;
import com.company.hrm.hr.repository.NhanVienRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * T14 - Unit test cho SeveranceCalculatorService.
 *
 * <p>Test cac nhanh:
 * <ul>
 *   <li>Tham nien duoi 12 thang -> khong co tro cap</li>
 *   <li>Tham nien 5 nam, nghi tu nguyen (HS = 0.5)</li>
 *   <li>Bi sa thai (HS = 1.0)</li>
 *   <li>Nghi huu (HS = 0)</li>
 *   <li>Luong am -> throw exception</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class SeveranceCalculatorServiceTest {

    @Mock
    SeveranceCalcRepository severanceRepo;

    @Mock
    OffboardingCaseRepository caseRepo;

    @Mock
    NhanVienRepository nhanVienRepo;

    @InjectMocks
    SeveranceCalculatorService service;

    private NhanVien nv5Nam;
    private NhanVien nv6Thang;

    @BeforeEach
    void setUp() {
        nv5Nam = new NhanVien();
        nv5Nam.setNhanVienId(UUID.randomUUID());
        nv5Nam.setMaNv("NV-001");
        nv5Nam.setHoTen("Nguyen Van Test");
        nv5Nam.setNgayVaoLam(LocalDate.of(2021, 1, 1));

        nv6Thang = new NhanVien();
        nv6Thang.setNhanVienId(UUID.randomUUID());
        nv6Thang.setMaNv("NV-002");
        nv6Thang.setHoTen("Tran Thi Moi");
        nv6Thang.setNgayVaoLam(LocalDate.of(2026, 1, 1));
    }

    @Test
    void nhanVienThamNienDuoi12Thang_khongCoTroCap() {
        when(nhanVienRepo.findById(nv6Thang.getNhanVienId())).thenReturn(Optional.of(nv6Thang));

        SeveranceCalcDto dto = service.preview(
                nv6Thang.getNhanVienId(),
                LocalDate.of(2026, 7, 1),
                new BigDecimal("10000000"),
                LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);

        assertThat(dto.getSoTienTroCap()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.getHeSo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.getGhiChu()).contains("12 thang");
    }

    @Test
    void nghiTuNguyen_5Nam_heSoBang0_5() {
        when(nhanVienRepo.findById(nv5Nam.getNhanVienId())).thenReturn(Optional.of(nv5Nam));

        SeveranceCalcDto dto = service.preview(
                nv5Nam.getNhanVienId(),
                LocalDate.of(2026, 1, 1),
                new BigDecimal("20000000"),
                LyDoNghiViec.NGHI_VIEC_TU_NGUYEN);

        // ChronoUnit.DAYS.between(2021-01-01, 2026-01-01) = 1826 days
        // 1826 / 30.4375 = 59.99 -> floor 59 thang = 4.92 nam
        // so_tien = 4.92 * 0.5 * 20tr = 49,200,000
        assertThat(dto.getThoiGianLamViecThang()).isEqualTo(59);
        assertThat(dto.getSoNamThamNien()).isEqualByComparingTo(new BigDecimal("4.92"));
        assertThat(dto.getHeSo()).isEqualByComparingTo(new BigDecimal("0.5"));
        assertThat(dto.getSoTienTroCap()).isEqualByComparingTo(new BigDecimal("49200000.00"));
    }

    @Test
    void biSaThai_5Nam_heSoBang1_0() {
        when(nhanVienRepo.findById(nv5Nam.getNhanVienId())).thenReturn(Optional.of(nv5Nam));

        SeveranceCalcDto dto = service.preview(
                nv5Nam.getNhanVienId(),
                LocalDate.of(2026, 1, 1),
                new BigDecimal("20000000"),
                LyDoNghiViec.SA_THAI);

        // 4.92 * 1.0 * 20tr = 98,400,000
        assertThat(dto.getHeSo()).isEqualByComparingTo(new BigDecimal("1.00"));
        assertThat(dto.getSoTienTroCap()).isEqualByComparingTo(new BigDecimal("98400000.00"));
    }

    @Test
    void nghiHuu_khongCoTroCap() {
        when(nhanVienRepo.findById(nv5Nam.getNhanVienId())).thenReturn(Optional.of(nv5Nam));

        SeveranceCalcDto dto = service.preview(
                nv5Nam.getNhanVienId(),
                LocalDate.of(2026, 1, 1),
                new BigDecimal("20000000"),
                LyDoNghiViec.NGHI_HUU);

        assertThat(dto.getHeSo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.getSoTienTroCap()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void luongAm_throwException() {
        when(nhanVienRepo.findById(any())).thenReturn(Optional.of(nv5Nam));

        assertThatThrownBy(() -> service.preview(
                nv5Nam.getNhanVienId(),
                LocalDate.of(2026, 1, 1),
                new BigDecimal("-1000000"),
                LyDoNghiViec.NGHI_VIEC_TU_NGUYEN))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_AVG_SALARY"));
    }

    @Test
    void nhanVienKhongTonTai_throwException() {
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.preview(
                UUID.randomUUID(),
                LocalDate.of(2026, 1, 1),
                new BigDecimal("10000000"),
                LyDoNghiViec.NGHI_VIEC_TU_NGUYEN))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("EMPLOYEE_NOT_FOUND"));
    }
}
