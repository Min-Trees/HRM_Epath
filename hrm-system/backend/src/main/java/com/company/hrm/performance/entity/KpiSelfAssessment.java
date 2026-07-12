package com.company.hrm.performance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_self_assessment", schema = "performance")
public class KpiSelfAssessment {

    @Id
    @GeneratedValue
    @Column(name = "self_assessment_id")
    private UUID selfAssessmentId;

    @Column(name = "assignment_id", nullable = false, unique = true)
    private UUID assignmentId;

    @Column(name = "actual_value", precision = 18, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "ty_le_hoan_thanh", precision = 5, scale = 2)
    private BigDecimal tyLeHoanThanh;

    @Column(name = "diem_tu_danh_gia", precision = 5, scale = 2)
    private BigDecimal diemTuDanhGia;

    @Column(name = "nhan_xet_nv", columnDefinition = "TEXT")
    private String nhanXetNv;

    @Column(name = "minh_chung_url", length = 500)
    private String minhChungUrl;

    @Column(name = "ngay_tu_danh_gia", nullable = false)
    private LocalDateTime ngayTuDanhGia = LocalDateTime.now();

    public UUID getSelfAssessmentId() { return selfAssessmentId; }
    public void setSelfAssessmentId(UUID selfAssessmentId) { this.selfAssessmentId = selfAssessmentId; }
    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }
    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
    public BigDecimal getTyLeHoanThanh() { return tyLeHoanThanh; }
    public void setTyLeHoanThanh(BigDecimal tyLeHoanThanh) { this.tyLeHoanThanh = tyLeHoanThanh; }
    public BigDecimal getDiemTuDanhGia() { return diemTuDanhGia; }
    public void setDiemTuDanhGia(BigDecimal diemTuDanhGia) { this.diemTuDanhGia = diemTuDanhGia; }
    public String getNhanXetNv() { return nhanXetNv; }
    public void setNhanXetNv(String nhanXetNv) { this.nhanXetNv = nhanXetNv; }
    public String getMinhChungUrl() { return minhChungUrl; }
    public void setMinhChungUrl(String minhChungUrl) { this.minhChungUrl = minhChungUrl; }
    public LocalDateTime getNgayTuDanhGia() { return ngayTuDanhGia; }
    public void setNgayTuDanhGia(LocalDateTime ngayTuDanhGia) { this.ngayTuDanhGia = ngayTuDanhGia; }
}
