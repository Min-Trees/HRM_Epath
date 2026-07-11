package com.company.hrm.hr.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "nguoi_phu_thuoc", schema = "hr")
public class NguoiPhuThuoc {

    @Id
    @GeneratedValue
    @Column(name = "nguoi_phu_thuoc_id")
    private UUID nguoiPhuThuocId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "ho_ten", nullable = false, length = 200)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "quan_he", nullable = false, length = 50)
    private String quanHe;

    @Column(name = "so_cccd_hoac_khai_sinh", length = 30)
    private String soCccdHoacKhaiSinh;

    @Column(name = "ma_so_thue_phu_thuoc", length = 20)
    private String maSoThuePhuThuoc;

    @Column(name = "tu_ngay_giam_tru", nullable = false)
    private LocalDate tuNgayGiamTru;

    @Column(name = "den_ngay_giam_tru")
    private LocalDate denNgayGiamTru;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    public UUID getNguoiPhuThuocId() { return nguoiPhuThuocId; }
    public void setNguoiPhuThuocId(UUID nguoiPhuThuocId) { this.nguoiPhuThuocId = nguoiPhuThuocId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getQuanHe() { return quanHe; }
    public void setQuanHe(String quanHe) { this.quanHe = quanHe; }
    public String getSoCccdHoacKhaiSinh() { return soCccdHoacKhaiSinh; }
    public void setSoCccdHoacKhaiSinh(String soCccdHoacKhaiSinh) { this.soCccdHoacKhaiSinh = soCccdHoacKhaiSinh; }
    public String getMaSoThuePhuThuoc() { return maSoThuePhuThuoc; }
    public void setMaSoThuePhuThuoc(String maSoThuePhuThuoc) { this.maSoThuePhuThuoc = maSoThuePhuThuoc; }
    public LocalDate getTuNgayGiamTru() { return tuNgayGiamTru; }
    public void setTuNgayGiamTru(LocalDate tuNgayGiamTru) { this.tuNgayGiamTru = tuNgayGiamTru; }
    public LocalDate getDenNgayGiamTru() { return denNgayGiamTru; }
    public void setDenNgayGiamTru(LocalDate denNgayGiamTru) { this.denNgayGiamTru = denNgayGiamTru; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
}