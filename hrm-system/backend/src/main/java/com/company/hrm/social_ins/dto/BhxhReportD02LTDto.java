package com.company.hrm.social_ins.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * T15 - Bao cao BHXH mau D02-LT (Bao tang/giam/ dieu chinh lao dong tham gia BHXH).
 *
 * <p>D02-LT la mau to khai theo Quyet dinh 595/QD-BHXH, dieu 27.
 * Moi dong tuong ung voi 1 nhan vien co bien dong BHXH trong ky bao cao.
 */
public class BhxhReportD02LTDto {

    private LocalDate tuNgay;
    private LocalDate denNgay;
    private String maDonViBHXH;
    private String tenDonVi;
    private String maSoThueDonVi;
    private LocalDateTime ngayLap;
    private String nguoiLap;
    private long tongSoDong;

    private List<Row> rows;

    public static class Row {
        private UUID nhanVienId;
        private String maNv;
        private String hoTen;
        private String soCmnd;
        private LocalDate ngaySinh;
        private String gioiTinh;
        private String quocTich;
        private String diaChi;
        private String maSoBhxh;

        /** TANG: bao tang moi; GIAM: bao giam (nghi viec/ het han HD); DCHINH: dieu chinh thong tin. */
        private String loaiBienDong;

        private LocalDate ngayPhatSinh;
        private String lyDoBienDong;

        /** Muc luong dong BHXH (thoi diem phat sinh). */
        private BigDecimal mucLuongDong;

        private BigDecimal tyLeNld;        // % NLĐ chiu (mac dinh 10.5)
        private BigDecimal tyLeDn;          // % DN chiu (mac dinh 21.5)

        private String noiDung;             // mo ta chi tiet
        private String trangThaiNop;        // CHUA_NOP, DA_NOP

        public UUID getNhanVienId() { return nhanVienId; }
        public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
        public String getMaNv() { return maNv; }
        public void setMaNv(String maNv) { this.maNv = maNv; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public String getSoCmnd() { return soCmnd; }
        public void setSoCmnd(String soCmnd) { this.soCmnd = soCmnd; }
        public LocalDate getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
        public String getGioiTinh() { return gioiTinh; }
        public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
        public String getQuocTich() { return quocTich; }
        public void setQuocTich(String quocTich) { this.quocTich = quocTich; }
        public String getDiaChi() { return diaChi; }
        public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
        public String getMaSoBhxh() { return maSoBhxh; }
        public void setMaSoBhxh(String maSoBhxh) { this.maSoBhxh = maSoBhxh; }
        public String getLoaiBienDong() { return loaiBienDong; }
        public void setLoaiBienDong(String loaiBienDong) { this.loaiBienDong = loaiBienDong; }
        public LocalDate getNgayPhatSinh() { return ngayPhatSinh; }
        public void setNgayPhatSinh(LocalDate ngayPhatSinh) { this.ngayPhatSinh = ngayPhatSinh; }
        public String getLyDoBienDong() { return lyDoBienDong; }
        public void setLyDoBienDong(String lyDoBienDong) { this.lyDoBienDong = lyDoBienDong; }
        public BigDecimal getMucLuongDong() { return mucLuongDong; }
        public void setMucLuongDong(BigDecimal mucLuongDong) { this.mucLuongDong = mucLuongDong; }
        public BigDecimal getTyLeNld() { return tyLeNld; }
        public void setTyLeNld(BigDecimal tyLeNld) { this.tyLeNld = tyLeNld; }
        public BigDecimal getTyLeDn() { return tyLeDn; }
        public void setTyLeDn(BigDecimal tyLeDn) { this.tyLeDn = tyLeDn; }
        public String getNoiDung() { return noiDung; }
        public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
        public String getTrangThaiNop() { return trangThaiNop; }
        public void setTrangThaiNop(String trangThaiNop) { this.trangThaiNop = trangThaiNop; }
    }

    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public String getMaDonViBHXH() { return maDonViBHXH; }
    public void setMaDonViBHXH(String maDonViBHXH) { this.maDonViBHXH = maDonViBHXH; }
    public String getTenDonVi() { return tenDonVi; }
    public void setTenDonVi(String tenDonVi) { this.tenDonVi = tenDonVi; }
    public String getMaSoThueDonVi() { return maSoThueDonVi; }
    public void setMaSoThueDonVi(String maSoThueDonVi) { this.maSoThueDonVi = maSoThueDonVi; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public String getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(String nguoiLap) { this.nguoiLap = nguoiLap; }
    public long getTongSoDong() { return tongSoDong; }
    public void setTongSoDong(long tongSoDong) { this.tongSoDong = tongSoDong; }
    public List<Row> getRows() { return rows; }
    public void setRows(List<Row> rows) { this.rows = rows; }
}
