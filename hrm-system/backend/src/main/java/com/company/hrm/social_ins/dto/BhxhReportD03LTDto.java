package com.company.hrm.social_ins.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * T15 - Bao cao BHXH mau D03-LT (Bao cap so BHXH / the cap so BHXH).
 *
 * <p>D03-LT theo Quyet dinh 595/QD-BHXH - Dung cho don vi de nghi cap so moi
 * (khi tuyen dung moi, mat so, hu so...) va ban giao the cho NV.
 */
public class BhxhReportD03LTDto {

    private LocalDate tuNgay;
    private LocalDate denNgay;
    private String maDonViBHXH;
    private String tenDonVi;
    private LocalDateTime ngayLap;
    private long tongSoDong;

    private List<Row> rows;

    public static class Row {
        private UUID nhanVienId;
        private String maNv;
        private String hoTen;
        private LocalDate ngaySinh;
        private String soCmnd;
        private String maSoBhxh;
        private LocalDate ngayCapSo;        // ngay cap so moi (neu bi trung, ngay nhap khai)
        private String loaiDeNghi;          // CAP_MOI / CAP_LAI / SUA_DOI / MAT
        private String lyDo;
        private String viTriLuuTru;
        private String trangThaiNop;

        public UUID getNhanVienId() { return nhanVienId; }
        public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
        public String getMaNv() { return maNv; }
        public void setMaNv(String maNv) { this.maNv = maNv; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public LocalDate getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
        public String getSoCmnd() { return soCmnd; }
        public void setSoCmnd(String soCmnd) { this.soCmnd = soCmnd; }
        public String getMaSoBhxh() { return maSoBhxh; }
        public void setMaSoBhxh(String maSoBhxh) { this.maSoBhxh = maSoBhxh; }
        public LocalDate getNgayCapSo() { return ngayCapSo; }
        public void setNgayCapSo(LocalDate ngayCapSo) { this.ngayCapSo = ngayCapSo; }
        public String getLoaiDeNghi() { return loaiDeNghi; }
        public void setLoaiDeNghi(String loaiDeNghi) { this.loaiDeNghi = loaiDeNghi; }
        public String getLyDo() { return lyDo; }
        public void setLyDo(String lyDo) { this.lyDo = lyDo; }
        public String getViTriLuuTru() { return viTriLuuTru; }
        public void setViTriLuuTru(String viTriLuuTru) { this.viTriLuuTru = viTriLuuTru; }
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
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public long getTongSoDong() { return tongSoDong; }
    public void setTongSoDong(long tongSoDong) { this.tongSoDong = tongSoDong; }
    public List<Row> getRows() { return rows; }
    public void setRows(List<Row> rows) { this.rows = rows; }
}
