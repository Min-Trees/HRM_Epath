package com.company.hrm.social_ins.service;

import com.company.hrm.social_ins.dto.BhxhReportD02LTDto;
import com.company.hrm.social_ins.dto.BhxhReportD03LTDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BhxhXmlExporterTest {

    private BhxhXmlExporter exporter;
    private BhxhReportD02LTDto d02;
    private BhxhReportD03LTDto d03;

    @BeforeEach
    void setUp() {
        exporter = new BhxhXmlExporter();

        d02 = new BhxhReportD02LTDto();
        d02.setTuNgay(LocalDate.of(2026, 1, 1));
        d02.setDenNgay(LocalDate.of(2026, 1, 31));
        d02.setMaDonViBHXH("DV-001");
        d02.setTenDonVi("Cong ty TNHH ABC");
        d02.setMaSoThueDonVi("0123456789");
        d02.setNgayLap(LocalDateTime.of(2026, 2, 1, 10, 30));
        d02.setNguoiLap("Nguyen Van A");

        List<BhxhReportD02LTDto.Row> rows = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BhxhReportD02LTDto.Row r = new BhxhReportD02LTDto.Row();
            r.setNhanVienId(UUID.randomUUID());
            r.setMaNv("NV-00" + i);
            r.setHoTen("Nguyen Van Test " + i);
            r.setSoCmnd("012345678" + i);
            r.setNgaySinh(LocalDate.of(1990, 1, 1));
            r.setGioiTinh("NAM");
            r.setMaSoBhxh("BH" + i + "00001");
            r.setLoaiBienDong(i == 1 ? "TANG" : "GIAM");
            r.setLyDoBienDong("Nghi viec");
            r.setNgayPhatSinh(LocalDate.of(2026, 1, 5 + i));
            r.setMucLuongDong(new BigDecimal("10000000"));
            r.setTyLeNld(new BigDecimal("10.5"));
            r.setTyLeDn(new BigDecimal("21.5"));
            r.setTrangThaiNop("CHUA_NOP");
            r.setNoiDung("Test row " + i);
            rows.add(r);
        }
        d02.setRows(rows);
        d02.setTongSoDong(rows.size());

        d03 = new BhxhReportD03LTDto();
        d03.setTuNgay(LocalDate.of(2026, 1, 1));
        d03.setDenNgay(LocalDate.of(2026, 1, 31));
        d03.setMaDonViBHXH("DV-001");
        d03.setTenDonVi("Cong ty TNHH ABC");
        d03.setNgayLap(LocalDateTime.now());

        List<BhxhReportD03LTDto.Row> rows3 = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            BhxhReportD03LTDto.Row r = new BhxhReportD03LTDto.Row();
            r.setNhanVienId(UUID.randomUUID());
            r.setMaNv("NV-009" + i);
            r.setHoTen("Tran Thi X " + i);
            r.setNgaySinh(LocalDate.of(1992, 3, i));
            r.setSoCmnd("02345678" + i);
            r.setMaSoBhxh("BH" + i + "99999");
            r.setNgayCapSo(LocalDate.of(2026, 1, 10 + i));
            r.setLoaiDeNghi(i == 1 ? "CAP_MOI" : "CAP_LAI");
            r.setLyDo(i == 1 ? "Tuyen dung moi" : "The BHXH bi hong");
            r.setViTriLuuTru("Phong HCNS");
            r.setTrangThaiNop("CHUA_NOP");
            rows3.add(r);
        }
        d03.setRows(rows3);
        d03.setTongSoDong(rows3.size());
    }

    private Map<String, Object> sampleD02Row() {
        Map<String, Object> row = new HashMap<>();
        row.put("nhan_vien_id", UUID.randomUUID());
        row.put("ma_nv", "NV-001");
        row.put("ho_ten", "Nguyen Van A");
        row.put("so_cmnd", "0123456789");
        row.put("ngay_sinh", java.sql.Date.valueOf("1990-01-01"));
        row.put("gioi_tinh", "NAM");
        row.put("quoc_tich", "VN");
        row.put("dia_chi_lien_lac", "Ha Noi");
        row.put("ma_so_bhxh", "BH001");
        row.put("loai_bao", "TANG");
        row.put("ly_do", "MOI_TUYEN_DUNG");
        row.put("ngay_phat_sinh", java.sql.Date.valueOf("2026-01-15"));
        row.put("da_nop", false);
        row.put("muc_luong_dong", new BigDecimal("10000000"));
        row.put("ty_le_dong_nld", new BigDecimal("10.5"));
        row.put("ty_le_dong_dn", new BigDecimal("21.5"));
        return row;
    }

    @Test
    void exportD02LT_coHeaderVaTongSoDong() {
        String xml = exporter.exportD02LT(d02);
        assertThat(xml).startsWith("<?xml");
        assertThat(xml).contains("BAO_CAO_D02_LT");
        assertThat(xml).contains("<MA_DON_VI>DV-001</MA_DON_VI>");
        assertThat(xml).contains("<TEN_DON_VI>Cong ty TNHH ABC</TEN_DON_VI>");
        assertThat(xml).contains("<TONG_SO_DONG>3</TONG_SO_DONG>");
        assertThat(xml).contains("<MA_NV>NV-001</MA_NV>");
        assertThat(xml).contains("</BAO_CAO_D02_LT>");
    }

    @Test
    void exportD02LT_dungKhiRowsTrong() {
        d02.setRows(null);
        d02.setTongSoDong(0);
        String xml = exporter.exportD02LT(d02);
        assertThat(xml).contains("<TONG_SO_DONG>0</TONG_SO_DONG>");
        assertThat(xml).contains("<DANH_SACH>");
        assertThat(xml).contains("</DANH_SACH>");
    }

    @Test
    void exportD03LT_coHeaderVaRowData() {
        String xml = exporter.exportD03LT(d03);
        assertThat(xml).contains("BAO_CAO_D03_LT");
        assertThat(xml).contains("<TONG_SO_DONG>2</TONG_SO_DONG>");
        assertThat(xml).contains("<LOAI_DE_NGHI>CAP_MOI</LOAI_DE_NGHI>");
        assertThat(xml).contains("<LOAI_DE_NGHI>CAP_LAI</LOAI_DE_NGHI>");
        assertThat(xml).contains("</BAO_CAO_D03_LT>");
    }

    @Test
    void xmlEncode_escapesSpecialCharacters() {
        BhxhReportD02LTDto.Row r = new BhxhReportD02LTDto.Row();
        r.setMaNv("NV-X");
        r.setHoTen("Test & < > \"quotes\"");
        d02.setRows(List.of(r));
        d02.setTongSoDong(1);

        String xml = exporter.exportD02LT(d02);
        assertThat(xml).contains("&amp;");
        assertThat(xml).contains("&lt;");
        assertThat(xml).contains("&gt;");
        assertThat(xml).contains("&quot;");
    }
}
