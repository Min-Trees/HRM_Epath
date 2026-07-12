package com.company.hrm.payroll.run.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * T19 - Phieu luong in PDF cho NV.
 *
 * <p>Cau truc: thong tin NV + chi tiet cac khoan (phu cap, thuong,
 * khau tru) + tong ket thuc linh.
 */
public class PayslipDto {

    private UUID payslipId;
    private String maPayslip;

    // Thong tin ky
    private Integer thang;
    private Integer nam;

    // Thong tin NV
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;
    private String phongBan;
    private String chucDanh;

    // Cac khoan thu
    private BigDecimal luongCoBan = BigDecimal.ZERO;
    private BigDecimal phuCap = BigDecimal.ZERO;
    private BigDecimal tienOt = BigDecimal.ZERO;
    private BigDecimal tongKhoanThu = BigDecimal.ZERO;

    // BHXH + Thue
    private BigDecimal mucDongBhxh = BigDecimal.ZERO;
    private BigDecimal bhxhNld = BigDecimal.ZERO;
    private BigDecimal thueTncn = BigDecimal.ZERO;

    // Giam tru
    private BigDecimal tamUng = BigDecimal.ZERO;
    private BigDecimal khauTruKhac = BigDecimal.ZERO;
    private BigDecimal tongKhoanTru = BigDecimal.ZERO;

    // Thuc linh
    private BigDecimal thucLinh = BigDecimal.ZERO;
    private List<LineItem> chiTietThuong;
    private List<LineItem> chiTietKhauTru;

    public static class LineItem {
        private String tenKhoan;
        private BigDecimal soTien;

        public LineItem() {}
        public LineItem(String tenKhoan, BigDecimal soTien) {
            this.tenKhoan = tenKhoan;
            this.soTien = soTien;
        }

        public String getTenKhoan() { return tenKhoan; }
        public void setTenKhoan(String tenKhoan) { this.tenKhoan = tenKhoan; }
        public BigDecimal getSoTien() { return soTien; }
        public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    }

    public UUID getPayslipId() { return payslipId; }
    public void setPayslipId(UUID payslipId) { this.payslipId = payslipId; }
    public String getMaPayslip() { return maPayslip; }
    public void setMaPayslip(String maPayslip) { this.maPayslip = maPayslip; }
    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getPhongBan() { return phongBan; }
    public void setPhongBan(String phongBan) { this.phongBan = phongBan; }
    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }
    public BigDecimal getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(BigDecimal luongCoBan) { this.luongCoBan = luongCoBan; }
    public BigDecimal getPhuCap() { return phuCap; }
    public void setPhuCap(BigDecimal phuCap) { this.phuCap = phuCap; }
    public BigDecimal getTienOt() { return tienOt; }
    public void setTienOt(BigDecimal tienOt) { this.tienOt = tienOt; }
    public BigDecimal getTongKhoanThu() { return tongKhoanThu; }
    public void setTongKhoanThu(BigDecimal tongKhoanThu) { this.tongKhoanThu = tongKhoanThu; }
    public BigDecimal getMucDongBhxh() { return mucDongBhxh; }
    public void setMucDongBhxh(BigDecimal mucDongBhxh) { this.mucDongBhxh = mucDongBhxh; }
    public BigDecimal getBhxhNld() { return bhxhNld; }
    public void setBhxhNld(BigDecimal bhxhNld) { this.bhxhNld = bhxhNld; }
    public BigDecimal getThueTncn() { return thueTncn; }
    public void setThueTncn(BigDecimal thueTncn) { this.thueTncn = thueTncn; }
    public BigDecimal getTamUng() { return tamUng; }
    public void setTamUng(BigDecimal tamUng) { this.tamUng = tamUng; }
    public BigDecimal getKhauTruKhac() { return khauTruKhac; }
    public void setKhauTruKhac(BigDecimal khauTruKhac) { this.khauTruKhac = khauTruKhac; }
    public BigDecimal getTongKhoanTru() { return tongKhoanTru; }
    public void setTongKhoanTru(BigDecimal tongKhoanTru) { this.tongKhoanTru = tongKhoanTru; }
    public BigDecimal getThucLinh() { return thucLinh; }
    public void setThucLinh(BigDecimal thucLinh) { this.thucLinh = thucLinh; }
    public List<LineItem> getChiTietThuong() { return chiTietThuong; }
    public void setChiTietThuong(List<LineItem> chiTietThuong) { this.chiTietThuong = chiTietThuong; }
    public List<LineItem> getChiTietKhauTru() { return chiTietKhauTru; }
    public void setChiTietKhauTru(List<LineItem> chiTietKhauTru) { this.chiTietKhauTru = chiTietKhauTru; }
}
