package com.company.hrm.payroll.tax.service;

import com.company.hrm.payroll.tax.dto.QuyetToan02QTTDto;
import com.company.hrm.payroll.tax.dto.QuyetToan05QTTDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuyetToanThueXmlExporterTest {

    private QuyetToanThueXmlExporter exporter;
    private QuyetToan02QTTDto d02;
    private QuyetToan05QTTDto d05;

    @BeforeEach
    void setUp() {
        exporter = new QuyetToanThueXmlExporter();

        d02 = new QuyetToan02QTTDto();
        d02.setNam(2026);
        d02.setMaDonVi("DV-001");
        d02.setTenDonVi("Cong ty TNHH ABC");
        d02.setMaSoThue("0123456789");
        d02.setNgayLap(LocalDateTime.of(2027, 1, 15, 10, 0));
        d02.setNguoiLap("admin");
        d02.setTongSoNhanVien(2);
        d02.setTongNhanVienUyQuyen(2);
        d02.setTongNhanVienTuQtt(0);
        d02.setTongThuNhapChiuThue(new BigDecimal("240000000"));
        d02.setTongGiamTruBanThan(new BigDecimal("220000000"));
        d02.setTongGiamTruNguoiPhuThuoc(new BigDecimal("0"));
        d02.setTongThueDaKhauTru(new BigDecimal("18000000"));
        d02.setTongThuePhaiNopThem(BigDecimal.ZERO);
        d02.setTongThueDuocHoan(BigDecimal.ZERO);
        d02.setTongThuePhaiNop(new BigDecimal("18000000"));

        List<QuyetToan02QTTDto.RowSummary> rows = new ArrayList<>();
        QuyetToan02QTTDto.RowSummary r1 = new QuyetToan02QTTDto.RowSummary();
        r1.setMaNv("NV-001");
        r1.setHoTen("Nguyen Van A");
        r1.setMaSoThue("0123456789-001");
        r1.setThuNhapChiuThue(new BigDecimal("150000000"));
        r1.setThueDaKhauTru(new BigDecimal("12000000"));
        r1.setThuePhaiNop(new BigDecimal("12000000"));
        rows.add(r1);
        d02.setTop10NhanVienThueCao(rows);

        d05 = new QuyetToan05QTTDto();
        d05.setNam(2026);
        d05.setNhanVienId(java.util.UUID.randomUUID());
        d05.setMaNv("NV-001");
        d05.setHoTen("Nguyen Van A");
        d05.setMaSoThue("0123456789-001");
        d05.setCmnd("0123456789");
        d05.setDiaChi("Ha Noi");
        d05.setNgaySinh(LocalDate.of(1990, 1, 1));
        d05.setSoSoBHXH("BH00001");
        d05.setLoaiCamKet08("UY_QUYEN_QTT");
        d05.setSoNguoiPhuThuoc(0);
        d05.setGiamTruBanThan(new BigDecimal("132000000"));
        d05.setGiamTruNguoiPhuThuoc(BigDecimal.ZERO);
        d05.setTenDonVi("Cong ty TNHH ABC");
        d05.setMaSoThueDonVi("0123456789");
        d05.setTongThuNhapCaNam(new BigDecimal("200000000"));
        d05.setTongThuNhapChiuThue(new BigDecimal("68000000"));
        d05.setTongThueDaKhauTru(new BigDecimal("12000000"));
        d05.setTongThuePhaiNop(new BigDecimal("12000000"));
        d05.setThueDuocHoan(BigDecimal.ZERO);

        List<QuyetToan05QTTDto.MonthlyRow> monthly = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            QuyetToan05QTTDto.MonthlyRow m = new QuyetToan05QTTDto.MonthlyRow();
            m.setThang(i);
            m.setThuNhapChiuThue(new BigDecimal("5666666"));
            m.setThueDaKhauTru(new BigDecimal("1000000"));
            m.setGiamTruBanThan(new BigDecimal("11000000"));
            m.setGiamTruNguoiPhuThuoc(BigDecimal.ZERO);
            monthly.add(m);
        }
        d05.setChiTietThang(monthly);
    }

    @Test
    void export02QTT_coHeaderVaTongHop() {
        String xml = exporter.export02QTT(d02);
        assertThat(xml).contains("QUYET_TOAN_02_QTT");
        assertThat(xml).contains("<NAM>2026</NAM>");
        assertThat(xml).contains("<MA_DON_VI>DV-001</MA_DON_VI>");
        assertThat(xml).contains("<TONG_SO_NHAN_VIEN>2</TONG_SO_NHAN_VIEN>");
        assertThat(xml).contains("<TONG_THUE_DA_KHAU_TRU>18000000</TONG_THUE_DA_KHAU_TRU>");
        assertThat(xml).contains("<MA_NV>NV-001</MA_NV>");
        assertThat(xml).contains("</QUYET_TOAN_02_QTT>");
    }

    @Test
    void export02QTT_dungKhiKhongCoNhanVien() {
        d02.setTop10NhanVienThueCao(null);
        d02.setTongSoNhanVien(0);
        String xml = exporter.export02QTT(d02);
        assertThat(xml).contains("<TONG_SO_NHAN_VIEN>0</TONG_SO_NHAN_VIEN>");
        assertThat(xml).contains("</QUYET_TOAN_02_QTT>");
    }

    @Test
    void export05QTT_coHeaderVaThongTinNV() {
        String xml = exporter.export05QTT(d05);
        assertThat(xml).contains("QUYET_TOAN_05_QTT");
        assertThat(xml).contains("<MA_NV>NV-001</MA_NV>");
        assertThat(xml).contains("<LOAI_CAM_KET_08>UY_QUYEN_QTT</LOAI_CAM_KET_08>");
        assertThat(xml).contains("CHI_TIET_THANG");
        assertThat(xml).contains("THANG_TT=\"1\"");
        assertThat(xml).contains("THANG_TT=\"12\"");
        assertThat(xml).contains("<TONG_THUE_DA_KHAU_TRU>12000000</TONG_THUE_DA_KHAU_TRU>");
        assertThat(xml).contains("</QUYET_TOAN_05_QTT>");
    }

    @Test
    void xmlEncode_escapesSpecialCharacters() {
        d02.setTenDonVi("Test & < > \"quotes\"");
        String xml = exporter.export02QTT(d02);
        assertThat(xml).contains("&amp;");
        assertThat(xml).contains("&lt;");
        assertThat(xml).contains("&gt;");
        assertThat(xml).contains("&quot;");
    }
}
