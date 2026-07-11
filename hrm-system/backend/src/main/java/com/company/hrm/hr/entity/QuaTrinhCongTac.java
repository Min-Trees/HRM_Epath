package com.company.hrm.hr.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "qua_trinh_cong_tac", schema = "hr")
public class QuaTrinhCongTac {

    @Id
    @GeneratedValue
    @Column(name = "qua_trinh_id")
    private UUID quaTrinhId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "don_vi", nullable = false, length = 300)
    private String donVi;

    @Column(name = "chuc_danh", length = 200)
    private String chucDanh;

    @Column(name = "tu_ngay", nullable = false)
    private LocalDate tuNgay;

    @Column(name = "den_ngay")
    private LocalDate denNgay;

    @Column(name = "mo_ta", columnDefinition = "text")
    private String moTa;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    public UUID getQuaTrinhId() { return quaTrinhId; }
    public void setQuaTrinhId(UUID quaTrinhId) { this.quaTrinhId = quaTrinhId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }
    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public java.time.Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
}