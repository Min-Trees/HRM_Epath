package com.company.hrm.payroll.run.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "khoan_luong", schema = "payroll")
public class KhoanLuong {

    @Id
    @GeneratedValue
    @Column(name = "khoan_id")
    private UUID khoanId;

    @Column(name = "bang_luong_id", nullable = false)
    private UUID bangLuongId;

    @Column(name = "ky_linh_id", nullable = false)
    private UUID kyLinhId;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "loai_khoan", nullable = false, columnDefinition = "payroll.loai_khoan_luong")
    private LoaiKhoanLuong loaiKhoan;

    @Column(name = "so_tien", precision = 14, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "mo_ta", length = 300)
    private String moTa;

    @Column(name = "nguoi_them_id")
    private UUID nguoiThemId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getKhoanId() { return khoanId; }
    public void setKhoanId(UUID khoanId) { this.khoanId = khoanId; }
    public UUID getBangLuongId() { return bangLuongId; }
    public void setBangLuongId(UUID bangLuongId) { this.bangLuongId = bangLuongId; }
    public UUID getKyLinhId() { return kyLinhId; }
    public void setKyLinhId(UUID kyLinhId) { this.kyLinhId = kyLinhId; }
    public LoaiKhoanLuong getLoaiKhoan() { return loaiKhoan; }
    public void setLoaiKhoan(LoaiKhoanLuong loaiKhoan) { this.loaiKhoan = loaiKhoan; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public UUID getNguoiThemId() { return nguoiThemId; }
    public void setNguoiThemId(UUID nguoiThemId) { this.nguoiThemId = nguoiThemId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
