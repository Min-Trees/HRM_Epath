package com.company.hrm.hr.dto;

import java.time.LocalDate;
import java.util.UUID;

public class HopDongExpiringItem {
    private UUID hopDongId;
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;
    private String soHopDong;
    private LocalDate ngayHetHieuLuc;
    private long soNgayConLai;

    public UUID getHopDongId() { return hopDongId; }
    public void setHopDongId(UUID hopDongId) { this.hopDongId = hopDongId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoHopDong() { return soHopDong; }
    public void setSoHopDong(String soHopDong) { this.soHopDong = soHopDong; }
    public LocalDate getNgayHetHieuLuc() { return ngayHetHieuLuc; }
    public void setNgayHetHieuLuc(LocalDate ngayHetHieuLuc) { this.ngayHetHieuLuc = ngayHetHieuLuc; }
    public long getSoNgayConLai() { return soNgayConLai; }
    public void setSoNgayConLai(long soNgayConLai) { this.soNgayConLai = soNgayConLai; }
}