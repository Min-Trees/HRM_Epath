package com.company.hrm.payroll.tax.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * T16 - Mau 05/QTT-TNCN: Chi tiet quyet toan thue cho 1 NV.
 *
 * <p>Bao gom thu nhap hang thang + tong ket cuoi nam, su dung de
 * noi len CQT.
 */
public class QuyetToan05QTTDto {

    private Integer nam;
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;
    private String maSoThue;
    private String cmnd;
    private String diaChi;
    private String tenDonVi;
    private String maSoThueDonVi;
    private LocalDate ngaySinh;
    private String soSoBHXH;
    private String loaiCamKet08;        // UY_QUYEN_QTT / NV_TU_QTT / CHUA_CO
    private Integer soNguoiPhuThuoc;
    private BigDecimal giamTruBanThan;
    private BigDecimal giamTruNguoiPhuThuoc;

    private List<MonthlyRow> chiTietThang;
    private BigDecimal tongThuNhapCaNam;
    private BigDecimal tongThuNhapChiuThue;
    private BigDecimal tongThueDaKhauTru;
    private BigDecimal tongThuePhaiNop;
    private BigDecimal thueDuocHoan;

    public static class MonthlyRow {
        private Integer thang;
        private BigDecimal thuNhapChiuThue;
        private BigDecimal thueDaKhauTru;
        private BigDecimal giamTruBanThan;
        private BigDecimal giamTruNguoiPhuThuoc;

        public Integer getThang() { return thang; }
        public void setThang(Integer thang) { this.thang = thang; }
        public BigDecimal getThuNhapChiuThue() { return thuNhapChiuThue; }
        public void setThuNhapChiuThue(BigDecimal thuNhapChiuThue) { this.thuNhapChiuThue = thuNhapChiuThue; }
        public BigDecimal getThueDaKhauTru() { return thueDaKhauTru; }
        public void setThueDaKhauTru(BigDecimal thueDaKhauTru) { this.thueDaKhauTru = thueDaKhauTru; }
        public BigDecimal getGiamTruBanThan() { return giamTruBanThan; }
        public void setGiamTruBanThan(BigDecimal giamTruBanThan) { this.giamTruBanThan = giamTruBanThan; }
        public BigDecimal getGiamTruNguoiPhuThuoc() { return giamTruNguoiPhuThuoc; }
        public void setGiamTruNguoiPhuThuoc(BigDecimal giamTruNguoiPhuThuoc) { this.giamTruNguoiPhuThuoc = giamTruNguoiPhuThuoc; }
    }

    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { this.cmnd = cmnd; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTenDonVi() { return tenDonVi; }
    public void setTenDonVi(String tenDonVi) { this.tenDonVi = tenDonVi; }
    public String getMaSoThueDonVi() { return maSoThueDonVi; }
    public void setMaSoThueDonVi(String maSoThueDonVi) { this.maSoThueDonVi = maSoThueDonVi; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getSoSoBHXH() { return soSoBHXH; }
    public void setSoSoBHXH(String soSoBHXH) { this.soSoBHXH = soSoBHXH; }
    public String getLoaiCamKet08() { return loaiCamKet08; }
    public void setLoaiCamKet08(String loaiCamKet08) { this.loaiCamKet08 = loaiCamKet08; }
    public Integer getSoNguoiPhuThuoc() { return soNguoiPhuThuoc; }
    public void setSoNguoiPhuThuoc(Integer soNguoiPhuThuoc) { this.soNguoiPhuThuoc = soNguoiPhuThuoc; }
    public BigDecimal getGiamTruBanThan() { return giamTruBanThan; }
    public void setGiamTruBanThan(BigDecimal giamTruBanThan) { this.giamTruBanThan = giamTruBanThan; }
    public BigDecimal getGiamTruNguoiPhuThuoc() { return giamTruNguoiPhuThuoc; }
    public void setGiamTruNguoiPhuThuoc(BigDecimal giamTruNguoiPhuThuoc) { this.giamTruNguoiPhuThuoc = giamTruNguoiPhuThuoc; }
    public List<MonthlyRow> getChiTietThang() { return chiTietThang; }
    public void setChiTietThang(List<MonthlyRow> chiTietThang) { this.chiTietThang = chiTietThang; }
    public BigDecimal getTongThuNhapCaNam() { return tongThuNhapCaNam; }
    public void setTongThuNhapCaNam(BigDecimal tongThuNhapCaNam) { this.tongThuNhapCaNam = tongThuNhapCaNam; }
    public BigDecimal getTongThuNhapChiuThue() { return tongThuNhapChiuThue; }
    public void setTongThuNhapChiuThue(BigDecimal tongThuNhapChiuThue) { this.tongThuNhapChiuThue = tongThuNhapChiuThue; }
    public BigDecimal getTongThueDaKhauTru() { return tongThueDaKhauTru; }
    public void setTongThueDaKhauTru(BigDecimal tongThueDaKhauTru) { this.tongThueDaKhauTru = tongThueDaKhauTru; }
    public BigDecimal getTongThuePhaiNop() { return tongThuePhaiNop; }
    public void setTongThuePhaiNop(BigDecimal tongThuePhaiNop) { this.tongThuePhaiNop = tongThuePhaiNop; }
    public BigDecimal getThueDuocHoan() { return thueDuocHoan; }
    public void setThueDuocHoan(BigDecimal thueDuocHoan) { this.thueDuocHoan = thueDuocHoan; }
}
