package com.company.hrm.payroll.run.service;

import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.repository.NhanVienRepository;
import com.company.hrm.payroll.run.dto.PayslipDto;
import com.company.hrm.payroll.run.entity.KyLinhLuong;
import com.company.hrm.payroll.run.entity.TrangThaiKyLuong;
import com.company.hrm.payroll.run.repository.KhoanLuongRepository;
import com.company.hrm.payroll.run.repository.KyLinhLuongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayslipGeneratorTest {

    @Mock JdbcTemplate jdbc;
    @Mock KyLinhLuongRepository kyRepo;
    @Mock KhoanLuongRepository khoanRepo;
    @Mock NhanVienRepository nvRepo;
    PayslipGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PayslipGenerator(jdbc, kyRepo, khoanRepo, nvRepo);
    }

    @Test
    void generateForNhanVien_taoPhieuLuongCoBan() {
        UUID nvId = UUID.randomUUID();
        NhanVien nv = new NhanVien();
        nv.setNhanVienId(nvId);
        nv.setMaNv("NV-001");
        nv.setHoTen("Nguyen Van A");
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(nv));

        Map<String, Object> row = new HashMap<>();
        row.put("luong_co_ban", new BigDecimal("15000000"));
        row.put("phu_cap", new BigDecimal("2000000"));
        row.put("tien_ot", new BigDecimal("1500000"));
        row.put("muc_bhxh", new BigDecimal("17000000"));
        row.put("bhxh_nld", new BigDecimal("1785000"));
        row.put("thue_tncn", new BigDecimal("500000"));
        row.put("tam_ung", new BigDecimal("2000000"));
        row.put("khau_tru_khac", BigDecimal.ZERO);
        row.put("thuc_linh", new BigDecimal("14215000"));
        when(jdbc.queryForList(any(String.class), eq(nvId), eq(5), eq(2026)))
                .thenReturn(List.of(row));

        when(jdbc.queryForObject(any(String.class), eq(UUID.class), eq(nvId), eq(5), eq(2026)))
                .thenReturn(UUID.randomUUID());
        when(khoanRepo.findByBangLuongId(any())).thenReturn(List.of());

        KyLinhLuong ky = new KyLinhLuong();
        ky.setKyLinhId(UUID.randomUUID());
        ky.setThang(5);
        ky.setNam(2026);
        ky.setTrangThai(TrangThaiKyLuong.DA_CHI_TRA);

        PayslipDto p = generator.generateForNhanVien(ky, nvId);
        assertThat(p.getMaNv()).isEqualTo("NV-001");
        assertThat(p.getHoTen()).isEqualTo("Nguyen Van A");
        assertThat(p.getLuongCoBan()).isEqualByComparingTo("15000000");
        assertThat(p.getTongKhoanThu()).isEqualByComparingTo("18500000"); // 15tr + 2tr + 1.5tr
        assertThat(p.getTongKhoanTru()).isEqualByComparingTo("4285000"); // BHXH + thue + tam ung
        assertThat(p.getThucLinh()).isEqualByComparingTo("14215000");
        assertThat(p.getChiTietThuong()).isNotEmpty();
        assertThat(p.getChiTietKhauTru()).hasSize(3); // BHXH + thue + tam ung
    }

    @Test
    void generateForNhanVien_khongCoBangLuong_throwException() {
        UUID nvId = UUID.randomUUID();
        when(nvRepo.findById(nvId)).thenReturn(Optional.of(new NhanVien()));
        when(jdbc.queryForList(any(String.class), eq(nvId), any(), any()))
                .thenReturn(List.of());

        KyLinhLuong ky = new KyLinhLuong();
        ky.setThang(5);
        ky.setNam(2026);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                generator.generateForNhanVien(ky, nvId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Chua co bang luong");
    }
}
