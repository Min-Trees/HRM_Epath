package com.company.hrm.payroll.tax.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.payroll.tax.dto.CamKet08Dto;
import com.company.hrm.payroll.tax.dto.QuyetToan02QTTDto;
import com.company.hrm.payroll.tax.dto.QuyetToan05QTTDto;
import com.company.hrm.payroll.tax.entity.CamKet08;
import com.company.hrm.payroll.tax.entity.LoaiCamKet08;
import com.company.hrm.payroll.tax.repository.CamKet08Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuyetToanThueServiceTest {

    @Mock JdbcTemplate jdbc;
    @Mock NhanVienRepository nhanVienRepo;
    @Mock CamKet08Repository camKet08Repo;

    QuyetToanThueService service;

    @BeforeEach
    void setUp() {
        service = new QuyetToanThueService(jdbc, nhanVienRepo, camKet08Repo);
        ReflectionTestUtils.setField(service, "defaultGiamTruBanThan", new BigDecimal("11000000"));
    }

    @Test
    void generate02QTT_thieuMaDonVi_throwException() {
        assertThatThrownBy(() -> service.generate02QTT(2026, "", null, null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("MISSING_MA_DON_VI"));
    }

    @Test
    void generate02QTT_namKhongHopLe_throwException() {
        assertThatThrownBy(() -> service.generate02QTT(1999, "DV-001", null, null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_NAM"));

        assertThatThrownBy(() -> service.generate02QTT(null, "DV-001", null, null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("INVALID_NAM"));
    }

    @Test
    void generate02QTT_rong_traVeTongSoNV0() {
        doReturn(new ArrayList<>())
                .when(jdbc).queryForList(anyString(), eq(2026));
        QuyetToan02QTTDto out = service.generate02QTT(2026, "DV-001", "Ten", "MST", "lap");
        assertThat(out.getTongSoNhanVien()).isZero();
        assertThat(out.getTongNhanVienUyQuyen()).isZero();
        assertThat(out.getTongThuNhapChiuThue()).isEqualByComparingTo("0");
    }

    @Test
    void generate02QTT_co2NV_mapDungTruong() {
        Map<String, Object> row1 = new HashMap<>();
        row1.put("nhan_vien_id", UUID.randomUUID());
        row1.put("ma_nv", "NV-001");
        row1.put("ho_ten", "Nguyen Van A");
        row1.put("ma_so_thue", "MST-001");
        row1.put("tong_thu_nhap_chiu_thue", new BigDecimal("100000000"));
        row1.put("tong_thue_da_khau_tru", new BigDecimal("5000000"));
        row1.put("tong_giam_tru_npt", BigDecimal.ZERO);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("nhan_vien_id", UUID.randomUUID());
        row2.put("ma_nv", "NV-002");
        row2.put("ho_ten", "Tran Thi B");
        row2.put("ma_so_thue", null);
        row2.put("tong_thu_nhap_chiu_thue", new BigDecimal("80000000"));
        row2.put("tong_thue_da_khau_tru", new BigDecimal("3000000"));
        row2.put("tong_giam_tru_npt", BigDecimal.ZERO);

        doReturn(new ArrayList<>(List.of(row1, row2)))
                .when(jdbc).queryForList(anyString(), eq(2026));
        when(jdbc.queryForObject(anyString(), eq(Integer.class), any(), anyInt())).thenReturn(0);

        QuyetToan02QTTDto out = service.generate02QTT(2026, "DV-001", "Ten", "MST", "lap");
        assertThat(out.getTongSoNhanVien()).isEqualTo(2);
        assertThat(out.getTongNhanVienUyQuyen()).isEqualTo(2); // mac dinh UY_QUYEN_QTT
        assertThat(out.getTongThueDaKhauTru()).isEqualByComparingTo("8000000");
        assertThat(out.getTongThuNhapChiuThue()).isEqualByComparingTo("180000000");
        assertThat(out.getTop10NhanVienThueCao()).hasSize(2);
        assertThat(out.getTop10NhanVienThueCao().get(0).getMaNv()).isEqualTo("NV-001");
    }

    @Test
    void generate05QTT_voiNVKhongTonTai_throwException() {
        when(nhanVienRepo.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.generate05QTT(2026, UUID.randomUUID(), null, null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                        .isEqualTo("EMPLOYEE_NOT_FOUND"));
    }

    @Test
    void generate05QTT_coLuong_thongKeTheoThang() {
        NhanVien nv = new NhanVien();
        nv.setNhanVienId(UUID.randomUUID());
        nv.setMaNv("NV-001");
        nv.setHoTen("Test User");
        nv.setSoCccd("0123456789");
        nv.setMaSoThue("MST-001");
        nv.setDiaChiLienLac("HN");
        nv.setNgaySinh(LocalDate.of(1990, 1, 1));

        when(nhanVienRepo.findById(nv.getNhanVienId())).thenReturn(Optional.of(nv));
        doReturn(new ArrayList<>())
                .when(jdbc).queryForList(anyString(), eq(nv.getNhanVienId()), eq(2026));
        when(camKet08Repo.findByNhanVienIdAndNam(any(), anyInt())).thenReturn(Optional.empty());
        when(jdbc.queryForObject(anyString(), eq(Integer.class), any())).thenReturn(0);

        QuyetToan05QTTDto out = service.generate05QTT(2026, nv.getNhanVienId(), "DN", "MST-DN");
        assertThat(out.getMaNv()).isEqualTo("NV-001");
        assertThat(out.getLoaiCamKet08()).isEqualTo("CHUA_CO");
        assertThat(out.getSoNguoiPhuThuoc()).isZero();
        assertThat(out.getChiTietThang()).isEmpty();
    }

    @Test
    void upsertCamKet08_taoMoi() {
        CamKet08Dto input = new CamKet08Dto();
        input.setNhanVienId(UUID.randomUUID());
        input.setNam(2026);
        input.setLoaiCamKet(LoaiCamKet08.UY_QUYEN_QTT);
        input.setNgayDangKy(LocalDate.of(2026, 1, 15));
        input.setHieuLucTuNgay(LocalDate.of(2026, 1, 1));

        when(camKet08Repo.findByNhanVienIdAndNam(input.getNhanVienId(), 2026))
                .thenReturn(Optional.empty());
        when(camKet08Repo.save(any())).thenAnswer(inv -> {
            CamKet08 c = inv.getArgument(0);
            c.setCamKetId(UUID.randomUUID());
            return c;
        });
        when(nhanVienRepo.findById(input.getNhanVienId())).thenReturn(Optional.empty());

        CamKet08Dto out = service.upsertCamKet08(input);
        assertThat(out.getCamKetId()).isNotNull();
        assertThat(out.getNam()).isEqualTo(2026);
        assertThat(out.getLoaiCamKet()).isEqualTo(LoaiCamKet08.UY_QUYEN_QTT);
        assertThat(out.getUyQuyenQtt()).isTrue();
    }

    @Test
    void upsertCamKet08_capNhat() {
        UUID camKetId = UUID.randomUUID();
        CamKet08 existing = new CamKet08();
        existing.setCamKetId(camKetId);
        existing.setNhanVienId(UUID.randomUUID());
        existing.setNam(2026);
        existing.setLoaiCamKet(LoaiCamKet08.UY_QUYEN_QTT);

        CamKet08Dto input = new CamKet08Dto();
        input.setNhanVienId(existing.getNhanVienId());
        input.setNam(2026);
        input.setLoaiCamKet(LoaiCamKet08.NV_TU_QTT);
        input.setNgayDangKy(LocalDate.of(2026, 2, 1));
        input.setHieuLucTuNgay(LocalDate.of(2026, 2, 1));

        when(camKet08Repo.findByNhanVienIdAndNam(existing.getNhanVienId(), 2026))
                .thenReturn(Optional.of(existing));
        when(camKet08Repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(nhanVienRepo.findById(existing.getNhanVienId())).thenReturn(Optional.empty());

        CamKet08Dto out = service.upsertCamKet08(input);
        assertThat(out.getCamKetId()).isEqualTo(camKetId);
        assertThat(out.getLoaiCamKet()).isEqualTo(LoaiCamKet08.NV_TU_QTT);
    }
}
