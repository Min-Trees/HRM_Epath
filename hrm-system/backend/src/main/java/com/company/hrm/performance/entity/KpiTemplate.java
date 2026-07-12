package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_template", schema = "performance")
public class KpiTemplate {

    @Id
    @GeneratedValue
    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "ten_mau", length = 200, nullable = false)
    private String tenMau;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "ap_dung_chuc_danh", length = 200)
    private String apDungChucDanh;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getTemplateId() { return templateId; }
    public void setTemplateId(UUID templateId) { this.templateId = templateId; }
    public String getTenMau() { return tenMau; }
    public void setTenMau(String tenMau) { this.tenMau = tenMau; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getApDungChucDanh() { return apDungChucDanh; }
    public void setApDungChucDanh(String apDungChucDanh) { this.apDungChucDanh = apDungChucDanh; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
