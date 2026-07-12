package com.company.hrm.hr.offboarding.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * T14 - Tai san can thu hoi khi nhan vien nghi viec.
 * (Laptop, the ra vao, CMND ban goc, ...)
 */
@Entity
@Table(name = "offboarding_asset", schema = "hr")
public class OffboardingAsset {

    @Id
    @GeneratedValue
    @Column(name = "asset_id")
    private UUID assetId;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Column(name = "ten_tai_san", nullable = false, length = 200)
    private String tenTaiSan;

    @Column(name = "ma_tai_san", length = 50)
    private String maTaiSan;

    @Column(name = "tinh_trang", length = 200)
    private String tinhTrang;

    @Column(name = "da_thu_hoi", nullable = false)
    private Boolean daThuHoi = false;

    @Column(name = "ngay_thu_hoi")
    private LocalDate ngayThuHoi;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public UUID getAssetId() { return assetId; }
    public void setAssetId(UUID assetId) { this.assetId = assetId; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public String getTenTaiSan() { return tenTaiSan; }
    public void setTenTaiSan(String tenTaiSan) { this.tenTaiSan = tenTaiSan; }
    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }
    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
    public Boolean getDaThuHoi() { return daThuHoi; }
    public void setDaThuHoi(Boolean daThuHoi) { this.daThuHoi = daThuHoi; }
    public LocalDate getNgayThuHoi() { return ngayThuHoi; }
    public void setNgayThuHoi(LocalDate ngayThuHoi) { this.ngayThuHoi = ngayThuHoi; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
