package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_assignment", schema = "performance")
public class KpiAssignment {

    @Id
    @GeneratedValue
    @Column(name = "assignment_id")
    private UUID assignmentId;

    @Column(name = "cycle_id", nullable = false)
    private UUID cycleId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "ten_muc_tieu", length = 300, nullable = false)
    private String tenMucTieu;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "loai_muc_tieu", nullable = false, columnDefinition = "performance.loai_muc_tieu")
    private LoaiMucTieu loaiMucTieu = LoaiMucTieu.KPI;

    @Column(name = "don_vi_do", length = 50)
    private String donViDo;

    @Column(name = "target_value", precision = 18, scale = 2, nullable = false)
    private BigDecimal targetValue;

    @Column(name = "trong_so", precision = 5, scale = 2, nullable = false)
    private BigDecimal trongSo = BigDecimal.ONE;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "TEXT")
    private String moTaChiTiet;

    @Column(name = "nguoi_gan_id")
    private UUID nguoiGanId;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "performance.trang_thai_assignment")
    private TrangThaiAssignment trangThai = TrangThaiAssignment.MOI_GAN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public UUID getTemplateId() { return templateId; }
    public void setTemplateId(UUID templateId) { this.templateId = templateId; }
    public String getTenMucTieu() { return tenMucTieu; }
    public void setTenMucTieu(String tenMucTieu) { this.tenMucTieu = tenMucTieu; }
    public LoaiMucTieu getLoaiMucTieu() { return loaiMucTieu; }
    public void setLoaiMucTieu(LoaiMucTieu loaiMucTieu) { this.loaiMucTieu = loaiMucTieu; }
    public String getDonViDo() { return donViDo; }
    public void setDonViDo(String donViDo) { this.donViDo = donViDo; }
    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }
    public BigDecimal getTrongSo() { return trongSo; }
    public void setTrongSo(BigDecimal trongSo) { this.trongSo = trongSo; }
    public String getMoTaChiTiet() { return moTaChiTiet; }
    public void setMoTaChiTiet(String moTaChiTiet) { this.moTaChiTiet = moTaChiTiet; }
    public UUID getNguoiGanId() { return nguoiGanId; }
    public void setNguoiGanId(UUID nguoiGanId) { this.nguoiGanId = nguoiGanId; }
    public TrangThaiAssignment getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiAssignment trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
