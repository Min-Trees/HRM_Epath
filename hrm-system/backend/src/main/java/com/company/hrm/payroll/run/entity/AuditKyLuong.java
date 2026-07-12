package com.company.hrm.payroll.run.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_ky_luong", schema = "payroll")
public class AuditKyLuong {

    @Id
    @GeneratedValue
    @Column(name = "audit_id")
    private UUID auditId;

    @Column(name = "ky_linh_id", nullable = false)
    private UUID kyLinhId;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai_cu", columnDefinition = "payroll.trang_thai_ky_luong")
    private TrangThaiKyLuong trangThaiCu;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai_moi", nullable = false, columnDefinition = "payroll.trang_thai_ky_luong")
    private TrangThaiKyLuong trangThaiMoi;

    @Column(name = "nguoi_thuc_hien_id")
    private UUID nguoiThucHienId;

    @Column(name = "hanh_dong", length = 50, nullable = false)
    private String hanhDong;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getAuditId() { return auditId; }
    public void setAuditId(UUID auditId) { this.auditId = auditId; }
    public UUID getKyLinhId() { return kyLinhId; }
    public void setKyLinhId(UUID kyLinhId) { this.kyLinhId = kyLinhId; }
    public TrangThaiKyLuong getTrangThaiCu() { return trangThaiCu; }
    public void setTrangThaiCu(TrangThaiKyLuong trangThaiCu) { this.trangThaiCu = trangThaiCu; }
    public TrangThaiKyLuong getTrangThaiMoi() { return trangThaiMoi; }
    public void setTrangThaiMoi(TrangThaiKyLuong trangThaiMoi) { this.trangThaiMoi = trangThaiMoi; }
    public UUID getNguoiThucHienId() { return nguoiThucHienId; }
    public void setNguoiThucHienId(UUID nguoiThucHienId) { this.nguoiThucHienId = nguoiThucHienId; }
    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
