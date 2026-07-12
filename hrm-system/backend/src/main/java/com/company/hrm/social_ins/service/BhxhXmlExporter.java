package com.company.hrm.social_ins.service;

import com.company.hrm.social_ins.dto.BhxhReportD02LTDto;
import com.company.hrm.social_ins.dto.BhxhReportD03LTDto;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * T15 - Serialize BHXH report D02-LT / D03-LT ra XML theo chuan
 * Quyet dinh 595/QD-BHXH (Phan mem QL BHXH VN).
 *
 * <p>Schema XML la dang giong format "BHXH" cua VN: moi dong bao cao
 * la 1 the {@code <ROWDATA>} voi day con cac truong.
 *
 * <p>Luu y: schema chinh thuc co the thay doi theo phien ban BHXH cua tung tinh.
 * Day la dang dong goi de frondend / client co the import vao he thong BHXH online.
 */
@Component
public class BhxhXmlExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String exportD02LT(BhxhReportD02LTDto r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<BAO_CAO_D02_LT xmlns=\"http://baohiemxahoi.gov.vn/schema/d02-lt\">\n");
        sb.append("  <THONG_TIN_CHUNG>\n");
        appendKhoa(sb, "MA_DON_VI", r.getMaDonViBHXH());
        appendKhoa(sb, "TEN_DON_VI", r.getTenDonVi());
        appendKhoa(sb, "MA_SO_THUE_DON_VI", r.getMaSoThueDonVi());
        appendKhoa(sb, "TU_NGAY", fmt(r.getTuNgay()));
        appendKhoa(sb, "DEN_NGAY", fmt(r.getDenNgay()));
        appendKhoa(sb, "NGAY_LAP", r.getNgayLap() != null
                ? r.getNgayLap().toLocalDate().format(DATE_FMT) : "");
        appendKhoa(sb, "NGUOI_LAP", r.getNguoiLap());
        appendKhoa(sb, "TONG_SO_DONG", String.valueOf(r.getTongSoDong()));
        sb.append("  </THONG_TIN_CHUNG>\n");
        sb.append("  <DANH_SACH>\n");
        if (r.getRows() != null) {
            for (BhxhReportD02LTDto.Row row : r.getRows()) {
                sb.append("    <ROWDATA STT=\"").append(r.getRows().indexOf(row) + 1).append("\">\n");
                appendKhoa(sb, "MA_NV", row.getMaNv());
                appendKhoa(sb, "HO_TEN", row.getHoTen());
                appendKhoa(sb, "SO_CMND", row.getSoCmnd());
                appendKhoa(sb, "NGAY_SINH", fmt(row.getNgaySinh()));
                appendKhoa(sb, "GIOI_TINH", row.getGioiTinh());
                appendKhoa(sb, "QUOC_TICH", row.getQuocTich());
                appendKhoa(sb, "DIA_CHI", row.getDiaChi());
                appendKhoa(sb, "MA_SO_BHXH", row.getMaSoBhxh());
                appendKhoa(sb, "LOAI_BAO", row.getLoaiBienDong());
                appendKhoa(sb, "LY_DO", row.getLyDoBienDong());
                appendKhoa(sb, "NGAY_PHAT_SINH", fmt(row.getNgayPhatSinh()));
                appendKhoa(sb, "MUC_LUONG_DONG", num(row.getMucLuongDong()));
                appendKhoa(sb, "TY_LE_NLD", num(row.getTyLeNld()));
                appendKhoa(sb, "TY_LE_DN", num(row.getTyLeDn()));
                appendKhoa(sb, "TRANG_THAI_NOP", row.getTrangThaiNop());
                appendKhoa(sb, "NOI_DUNG", row.getNoiDung());
                sb.append("    </ROWDATA>\n");
            }
        }
        sb.append("  </DANH_SACH>\n");
        sb.append("</BAO_CAO_D02_LT>\n");
        return sb.toString();
    }

    public String exportD03LT(BhxhReportD03LTDto r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<BAO_CAO_D03_LT xmlns=\"http://baohiemxahoi.gov.vn/schema/d03-lt\">\n");
        sb.append("  <THONG_TIN_CHUNG>\n");
        appendKhoa(sb, "MA_DON_VI", r.getMaDonViBHXH());
        appendKhoa(sb, "TEN_DON_VI", r.getTenDonVi());
        appendKhoa(sb, "TU_NGAY", fmt(r.getTuNgay()));
        appendKhoa(sb, "DEN_NGAY", fmt(r.getDenNgay()));
        appendKhoa(sb, "NGAY_LAP", r.getNgayLap() != null
                ? r.getNgayLap().toLocalDate().format(DATE_FMT) : "");
        appendKhoa(sb, "TONG_SO_DONG", String.valueOf(r.getTongSoDong()));
        sb.append("  </THONG_TIN_CHUNG>\n");
        sb.append("  <DANH_SACH>\n");
        if (r.getRows() != null) {
            int stt = 1;
            for (BhxhReportD03LTDto.Row row : r.getRows()) {
                sb.append("    <ROWDATA STT=\"").append(stt++).append("\">\n");
                appendKhoa(sb, "MA_NV", row.getMaNv());
                appendKhoa(sb, "HO_TEN", row.getHoTen());
                appendKhoa(sb, "NGAY_SINH", fmt(row.getNgaySinh()));
                appendKhoa(sb, "SO_CMND", row.getSoCmnd());
                appendKhoa(sb, "MA_SO_BHXH", row.getMaSoBhxh());
                appendKhoa(sb, "NGAY_CAP_SO", fmt(row.getNgayCapSo()));
                appendKhoa(sb, "LOAI_DE_NGHI", row.getLoaiDeNghi());
                appendKhoa(sb, "LY_DO", row.getLyDo());
                appendKhoa(sb, "VI_TRI_LUU_TRU", row.getViTriLuuTru());
                appendKhoa(sb, "TRANG_THAI_NOP", row.getTrangThaiNop());
                sb.append("    </ROWDATA>\n");
            }
        }
        sb.append("  </DANH_SACH>\n");
        sb.append("</BAO_CAO_D03_LT>\n");
        return sb.toString();
    }

    private static void appendKhoa(StringBuilder sb, String key, String value) {
        sb.append("    <").append(key).append(">")
                .append(xmlEscape(value))
                .append("</").append(key).append(">\n");
    }

    private static String num(BigDecimal v) { return v == null ? "" : v.toPlainString(); }
    private static String fmt(LocalDate d) { return d == null ? "" : d.format(DATE_FMT); }

    private static String xmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
