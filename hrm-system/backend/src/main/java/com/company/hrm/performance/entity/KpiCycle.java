package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_cycle", schema = "performance")
public class KpiCycle {

    @Id
    @GeneratedValue
    @Column(name = "cycle_id")
    private UUID cycleId;

    @Column(name = "ten_chu_ky", length = 100, nullable = false)
    private String tenChuKy;

    @Column(name = "loai_chu_ky", length = 20, nullable = false)
    private String loaiChuKy;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "han_nv_tu_danh_gia")
    private LocalDate hanNvTuDanhGia;

    @Column(name = "han_manager_review")
    private LocalDate hanManagerReview;

    @Column(name = "han_hr_phe_duyet")
    private LocalDate hanHrPheDuyet;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "performance.trang_thai_chu_ky")
    private TrangThaiChuKy trangThai = TrangThaiChuKy.MOI_TAO;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getCycleId() { return cycleId; }
    public void setCycleId(UUID cycleId) { this.cycleId = cycleId; }
    public String getTenChuKy() { return tenChuKy; }
    public void setTenChuKy(String tenChuKy) { this.tenChuKy = tenChuKy; }
    public String getLoaiChuKy() { return loaiChuKy; }
    public void setLoaiChuKy(String loaiChuKy) { this.loaiChuKy = loaiChuKy; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public LocalDate getHanNvTuDanhGia() { return hanNvTuDanhGia; }
    public void setHanNvTuDanhGia(LocalDate hanNvTuDanhGia) { this.hanNvTuDanhGia = hanNvTuDanhGia; }
    public LocalDate getHanManagerReview() { return hanManagerReview; }
    public void setHanManagerReview(LocalDate hanManagerReview) { this.hanManagerReview = hanManagerReview; }
    public LocalDate getHanHrPheDuyet() { return hanHrPheDuyet; }
    public void setHanHrPheDuyet(LocalDate hanHrPheDuyet) { this.hanHrPheDuyet = hanHrPheDuyet; }
    public TrangThaiChuKy getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiChuKy trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
