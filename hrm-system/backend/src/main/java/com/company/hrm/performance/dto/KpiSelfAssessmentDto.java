package com.company.hrm.performance.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class KpiSelfAssessmentDto {

    private UUID selfAssessmentId;

    @NotNull
    private UUID assignmentId;

    private BigDecimal actualValue;
    private BigDecimal tyLeHoanThanh;

    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemTuDanhGia;

    private String nhanXetNv;
    private String minhChungUrl;
    private LocalDateTime ngayTuDanhGia;

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
