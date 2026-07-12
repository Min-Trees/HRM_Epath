package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_review", schema = "performance")
public class KpiReview {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private UUID reviewId;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private UUID assignmentId;

    @Column(name = "nguoi_review_id", nullable = false)
    private UUID nguoiReviewId;

    @Column(name = "actual_value", precision = 18, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "diem_manager", precision = 5, scale = 2, nullable = false)
    private BigDecimal diemManager;

    @Column(name = "diem_trung_binh", insertable = false, updatable = false)
    private BigDecimal diemTrungBinh;

    @Column(name = "nhan_xet_manager", columnDefinition = "TEXT")
    private String nhanXetManager;

    @Column(name = "diem_manh", columnDefinition = "TEXT")
    private String diemManh;

    @Column(name = "diem_yeu", columnDefinition = "TEXT")
    private String diemYeu;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "de_xuat_xep_loai", columnDefinition = "performance.xep_loai")
    private XepLoai deXuatXepLoai;

    @Column(name = "ngay_review", nullable = false)
    private LocalDateTime ngayReview = LocalDateTime.now();

    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }
    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public UUID getNguoiReviewId() { return nguoiReviewId; }
    public void setNguoiReviewId(UUID nguoiReviewId) { this.nguoiReviewId = nguoiReviewId; }
    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
    public BigDecimal getDiemManager() { return diemManager; }
    public void setDiemManager(BigDecimal diemManager) { this.diemManager = diemManager; }
    public BigDecimal getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(BigDecimal diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }
    public String getNhanXetManager() { return nhanXetManager; }
    public void setNhanXetManager(String nhanXetManager) { this.nhanXetManager = nhanXetManager; }
    public String getDiemManh() { return diemManh; }
    public void setDiemManh(String diemManh) { this.diemManh = diemManh; }
    public String getDiemYeu() { return diemYeu; }
    public void setDiemYeu(String diemYeu) { this.diemYeu = diemYeu; }
    public XepLoai getDeXuatXepLoai() { return deXuatXepLoai; }
    public void setDeXuatXepLoai(XepLoai deXuatXepLoai) { this.deXuatXepLoai = deXuatXepLoai; }
    public LocalDateTime getNgayReview() { return ngayReview; }
    public void setNgayReview(LocalDateTime ngayReview) { this.ngayReview = ngayReview; }
}
