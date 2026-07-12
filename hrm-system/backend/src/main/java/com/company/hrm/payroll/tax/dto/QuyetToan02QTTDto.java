package com.company.hrm.payroll.tax.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * T16 - Mau 02/QTT-TNCN: Tong hop quyet toan thue TNCN cua toan DN.
 *
 * <p>Theo Thong tu 92/2015, phu luc 02/QTT bao gom:
 * <ul>
 *   <li>Tong so NV da cap MST tai thoi diem 31/12</li>
 *   <li>Tong so NV dang ky QTT (uy quyen cho DN)</li>
 *   <li>Tong thu nhap chiu thue toan DN</li>
 *   <li>Tong giam tru ban than</li>
 *   <li>Tong giam tru NPT</li>
 *   <li>Tong thue TNCN da khau tru</li>
 *   <li>Tong thue TNCN phai nop them (neu co)</li>
 *   <li>Tong thue TNCN duoc hoan (neu co)</li>
 * </ul>
 */
public class QuyetToan02QTTDto {

    private Integer nam;
    private String maDonVi;
    private String tenDonVi;
    private String maSoThue;
    private LocalDateTime ngayLap;
    private String nguoiLap;

    private Integer tongSoNhanVien;
    private Integer tongNhanVienUyQuyen;
    private Integer tongNhanVienTuQtt;

    private BigDecimal tongThuNhapChiuThue;
    private BigDecimal tongGiamTruBanThan;
    private BigDecimal tongGiamTruNguoiPhuThuoc;
    private BigDecimal tongThueDaKhauTru;
    private BigDecimal tongThuePhaiNopThem;
    private BigDecimal tongThueDuocHoan;
    private BigDecimal tongThuePhaiNop;

    private List<RowSummary> top10NhanVienThueCao;

    public static class RowSummary {
        private String maNv;
        private String hoTen;
        private String maSoThue;
        private BigDecimal thuNhapChiuThue;
        private BigDecimal thueDaKhauTru;
        private BigDecimal thuePhaiNop;

        public String getMaNv() { return maNv; }
        public void setMaNv(String maNv) { this.maNv = maNv; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getMaSoThue() { return maSoThue; }
        public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
        public BigDecimal getThuNhapChiuThue() { return thuNhapChiuThue; }
        public void setThuNhapChiuThue(BigDecimal thuNhapChiuThue) { this.thuNhapChiuThue = thuNhapChiuThue; }
        public BigDecimal getThueDaKhauTru() { return thueDaKhauTru; }
        public void setThueDaKhauTru(BigDecimal thueDaKhauTru) { this.thueDaKhauTru = thueDaKhauTru; }
        public BigDecimal getThuePhaiNop() { return thuePhaiNop; }
        public void setThuePhaiNop(BigDecimal thuePhaiNop) { this.thuePhaiNop = thuePhaiNop; }
    }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public String getMaDonVi() { return maDonVi; }
    public void setMaDonVi(String maDonVi) { this.maDonVi = maDonVi; }
    public String getTenDonVi() { return tenDonVi; }
    public void setTenDonVi(String tenDonVi) { this.tenDonVi = tenDonVi; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public String getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(String nguoiLap) { this.nguoiLap = nguoiLap; }
    public Integer getTongSoNhanVien() { return tongSoNhanVien; }
    public void setTongSoNhanVien(Integer tongSoNhanVien) { this.tongSoNhanVien = tongSoNhanVien; }
    public Integer getTongNhanVienUyQuyen() { return tongNhanVienUyQuyen; }
    public void setTongNhanVienUyQuyen(Integer tongNhanVienUyQuyen) { this.tongNhanVienUyQuyen = tongNhanVienUyQuyen; }
    public Integer getTongNhanVienTuQtt() { return tongNhanVienTuQtt; }
    public void setTongNhanVienTuQtt(Integer tongNhanVienTuQtt) { this.tongNhanVienTuQtt = tongNhanVienTuQtt; }
    public BigDecimal getTongThuNhapChiuThue() { return tongThuNhapChiuThue; }
    public void setTongThuNhapChiuThue(BigDecimal tongThuNhapChiuThue) { this.tongThuNhapChiuThue = tongThuNhapChiuThue; }
    public BigDecimal getTongGiamTruBanThan() { return tongGiamTruBanThan; }
    public void setTongGiamTruBanThan(BigDecimal tongGiamTruBanThan) { this.tongGiamTruBanThan = tongGiamTruBanThan; }
    public BigDecimal getTongGiamTruNguoiPhuThuoc() { return tongGiamTruNguoiPhuThuoc; }
    public void setTongGiamTruNguoiPhuThuoc(BigDecimal tongGiamTruNguoiPhuThuoc) { this.tongGiamTruNguoiPhuThuoc = tongGiamTruNguoiPhuThuoc; }
    public BigDecimal getTongThueDaKhauTru() { return tongThueDaKhauTru; }
    public void setTongThueDaKhauTru(BigDecimal tongThueDaKhauTru) { this.tongThueDaKhauTru = tongThueDaKhauTru; }
    public BigDecimal getTongThuePhaiNopThem() { return tongThuePhaiNopThem; }
    public void setTongThuePhaiNopThem(BigDecimal tongThuePhaiNopThem) { this.tongThuePhaiNopThem = tongThuePhaiNopThem; }
    public BigDecimal getTongThueDuocHoan() { return tongThueDuocHoan; }
    public void setTongThueDuocHoan(BigDecimal tongThueDuocHoan) { this.tongThueDuocHoan = tongThueDuocHoan; }
    public BigDecimal getTongThuePhaiNop() { return tongThuePhaiNop; }
    public void setTongThuePhaiNop(BigDecimal tongThuePhaiNop) { this.tongThuePhaiNop = tongThuePhaiNop; }
    public List<RowSummary> getTop10NhanVienThueCao() { return top10NhanVienThueCao; }
    public void setTop10NhanVienThueCao(List<RowSummary> top10NhanVienThueCao) { this.top10NhanVienThueCao = top10NhanVienThueCao; }
}
