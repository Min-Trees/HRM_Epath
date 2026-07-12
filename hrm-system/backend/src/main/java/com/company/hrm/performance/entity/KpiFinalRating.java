package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_final_rating", schema = "performance")
public class KpiFinalRating {

    @Id
    @GeneratedValue
    @Column(name = "final_rating_id")
    private UUID finalRatingId;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private UUID assignmentId;

    @Column(name = "nguoi_phe_duyet_id", nullable = false)
    private UUID nguoiPheDuyetId;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "xep_loai_cuoi", nullable = false, columnDefinition = "performance.xep_loai")
    private XepLoai xepLoaiCuoi;

    @Column(name = "diem_cuoi", precision = 5, scale = 2, nullable = false)
    private BigDecimal diemCuoi;

    @Column(name = "nhan_xet_hr", columnDefinition = "TEXT")
    private String nhanXetHr;

    @Column(name = "he_so_thuong", precision = 4, scale = 2)
    private BigDecimal heSoThuong;

    @Column(name = "ngay_phe_duyet", nullable = false)
    private LocalDateTime ngayPheDuyet = LocalDateTime.now();

    public UUID getFinalRatingId() { return finalRatingId; }
    public void setFinalRatingId(UUID finalRatingId) { this.finalRatingId = finalRatingId; }
    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public UUID getNguoiPheDuyetId() { return nguoiPheDuyetId; }
    public void setNguoiPheDuyetId(UUID nguoiPheDuyetId) { this.nguoiPheDuyetId = nguoiPheDuyetId; }
    public XepLoai getXepLoaiCuoi() { return xepLoaiCuoi; }
    public void setXepLoaiCuoi(XepLoai xepLoaiCuoi) { this.xepLoaiCuoi = xepLoaiCuoi; }
    public BigDecimal getDiemCuoi() { return diemCuoi; }
    public void setDiemCuoi(BigDecimal diemCuoi) { this.diemCuoi = diemCuoi; }
    public String getNhanXetHr() { return nhanXetHr; }
    public void setNhanXetHr(String nhanXetHr) { this.nhanXetHr = nhanXetHr; }
    public BigDecimal getHeSoThuong() { return heSoThuong; }
    public void setHeSoThuong(BigDecimal heSoThuong) { this.heSoThuong = heSoThuong; }
    public LocalDateTime getNgayPheDuyet() { return ngayPheDuyet; }
    public void setNgayPheDuyet(LocalDateTime ngayPheDuyet) { this.ngayPheDuyet = ngayPheDuyet; }
}
