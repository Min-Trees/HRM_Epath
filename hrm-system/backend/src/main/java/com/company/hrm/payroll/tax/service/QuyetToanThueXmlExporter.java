package com.company.hrm.payroll.tax.service;

import com.company.hrm.payroll.tax.dto.QuyetToan02QTTDto;
import com.company.hrm.payroll.tax.dto.QuyetToan05QTTDto;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * T16 - Serialize quyet toan thue TNCN ra XML.
 *
 * <p>Format theo phu luc Thong tu 92/2015 (schema XML khong chinh thuc
 * nhung tuong thich voi nhieu import tool cua VN).
 */
@Component
public class QuyetToanThueXmlExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String export02QTT(QuyetToan02QTTDto r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<QUYET_TOAN_02_QTT xmlns=\"http://gdt.gov.vn/schema/02qtt\">\n");
        sb.append("  <THONG_TIN_CHUNG>\n");
        kv(sb, "NAM", String.valueOf(r.getNam()));
        kv(sb, "MA_DON_VI", r.getMaDonVi());
        kv(sb, "TEN_DON_VI", r.getTenDonVi());
        kv(sb, "MA_SO_THUE", r.getMaSoThue());
        kv(sb, "NGAY_LAP", r.getNgayLap() != null ? r.getNgayLap().toLocalDate().format(DATE_FMT) : "");
        kv(sb, "NGUOI_LAP", r.getNguoiLap());
        sb.append("  </THONG_TIN_CHUNG>\n");
        sb.append("  <TONG_HOP>\n");
        kv(sb, "TONG_SO_NHAN_VIEN", String.valueOf(r.getTongSoNhanVien()));
        kv(sb, "TONG_NV_UY_QUYEN", String.valueOf(r.getTongNhanVienUyQuyen()));
        kv(sb, "TONG_NV_TU_QTT", String.valueOf(r.getTongNhanVienTuQtt()));
        kv(sb, "TONG_THU_NHAP_CHIU_THUE", num(r.getTongThuNhapChiuThue()));
        kv(sb, "TONG_GIAM_TRU_BAN_THAN", num(r.getTongGiamTruBanThan()));
        kv(sb, "TONG_GIAM_TRU_NGUOI_PHU_THUOC", num(r.getTongGiamTruNguoiPhuThuoc()));
        kv(sb, "TONG_THUE_DA_KHAU_TRU", num(r.getTongThueDaKhauTru()));
        kv(sb, "TONG_THUE_PHAI_NOP_THEM", num(r.getTongThuePhaiNopThem()));
        kv(sb, "TONG_THUE_DUOC_HOAN", num(r.getTongThueDuocHoan()));
        kv(sb, "TONG_THUE_PHAI_NOP", num(r.getTongThuePhaiNop()));
        sb.append("  </TONG_HOP>\n");
        if (r.getTop10NhanVienThueCao() != null) {
            sb.append("  <TOP_NHAN_VIEN_THUE_CAO>\n");
            int stt = 1;
            for (QuyetToan02QTTDto.RowSummary row : r.getTop10NhanVienThueCao()) {
                sb.append("    <ROWDATA STT=\"").append(stt++).append("\">\n");
                kv(sb, "MA_NV", row.getMaNv());
                kv(sb, "HO_TEN", row.getHoTen());
                kv(sb, "MA_SO_THUE", row.getMaSoThue());
                kv(sb, "THU_NHAP_CHIU_THUE", num(row.getThuNhapChiuThue()));
                kv(sb, "THUE_DA_KHAU_TRU", num(row.getThueDaKhauTru()));
                kv(sb, "THUE_PHAI_NOP", num(row.getThuePhaiNop()));
                sb.append("    </ROWDATA>\n");
            }
            sb.append("  </TOP_NHAN_VIEN_THUE_CAO>\n");
        }
        sb.append("</QUYET_TOAN_02_QTT>\n");
        return sb.toString();
    }

    public String export05QTT(QuyetToan05QTTDto r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<QUYET_TOAN_05_QTT xmlns=\"http://gdt.gov.vn/schema/05qtt\">\n");
        sb.append("  <THONG_TIN_NHAN_VIEN>\n");
        kv(sb, "NAM", String.valueOf(r.getNam()));
        kv(sb, "MA_NV", r.getMaNv());
        kv(sb, "HO_TEN", r.getHoTen());
        kv(sb, "MA_SO_THUE", r.getMaSoThue());
        kv(sb, "CMND", r.getCmnd());
        kv(sb, "DIA_CHI", r.getDiaChi());
        kv(sb, "NGAY_SINH", r.getNgaySinh() != null ? r.getNgaySinh().format(DATE_FMT) : "");
        kv(sb, "SO_SO_BHXH", r.getSoSoBHXH());
        kv(sb, "LOAI_CAM_KET_08", r.getLoaiCamKet08());
        kv(sb, "SO_NGUOI_PHU_THUOC", String.valueOf(r.getSoNguoiPhuThuoc()));
        kv(sb, "GIAM_TRU_BAN_THAN", num(r.getGiamTruBanThan()));
        kv(sb, "GIAM_TRU_NGUOI_PHU_THUOC", num(r.getGiamTruNguoiPhuThuoc()));
        kv(sb, "TEN_DON_VI", r.getTenDonVi());
        kv(sb, "MA_SO_THUE_DON_VI", r.getMaSoThueDonVi());
        sb.append("  </THONG_TIN_NHAN_VIEN>\n");
        sb.append("  <CHI_TIET_THANG>\n");
        if (r.getChiTietThang() != null) {
            for (QuyetToan05QTTDto.MonthlyRow m : r.getChiTietThang()) {
                sb.append("    <THANG THANG_TT=\"").append(m.getThang()).append("\">\n");
                kv(sb, "THU_NHAP_CHIU_THUE", num(m.getThuNhapChiuThue()));
                kv(sb, "THUE_DA_KHAU_TRU", num(m.getThueDaKhauTru()));
                kv(sb, "GIAM_TRU_BAN_THAN", num(m.getGiamTruBanThan()));
                kv(sb, "GIAM_TRU_NGUOI_PHU_THUOC", num(m.getGiamTruNguoiPhuThuoc()));
                sb.append("    </THANG>\n");
            }
        }
        sb.append("  </CHI_TIET_THANG>\n");
        sb.append("  <TONG_KET>\n");
        kv(sb, "TONG_THU_NHAP_CA_NAM", num(r.getTongThuNhapCaNam()));
        kv(sb, "TONG_THU_NHAP_CHIU_THUE", num(r.getTongThuNhapChiuThue()));
        kv(sb, "TONG_THUE_DA_KHAU_TRU", num(r.getTongThueDaKhauTru()));
        kv(sb, "TONG_THUE_PHAI_NOP", num(r.getTongThuePhaiNop()));
        kv(sb, "THUE_DUOC_HOAN", num(r.getThueDuocHoan()));
        sb.append("  </TONG_KET>\n");
        sb.append("</QUYET_TOAN_05_QTT>\n");
        return sb.toString();
    }

    private static void kv(StringBuilder sb, String key, String value) {
        sb.append("    <").append(key).append(">")
                .append(xmlEsc(value)).append("</").append(key).append(">\n");
    }

    private static String num(java.math.BigDecimal v) {
        return v == null ? "0" : v.toPlainString();
    }

    private static String xmlEsc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
