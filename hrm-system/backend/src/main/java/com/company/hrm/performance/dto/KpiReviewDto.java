package com.company.hrm.performance.dto;

import com.company.hrm.performance.entity.XepLoai;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class KpiReviewDto {

    private UUID reviewId;

    @NotNull
    private UUID assignmentId;

    @NotNull
    private UUID nguoiReviewId;

    private BigDecimal actualValue;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemManager;

    private BigDecimal diemTrungBinh;
    private String nhanXetManager;
    private String diemManh;
    private String diemYeu;
    private XepLoai deXuatXepLoai;
    private LocalDateTime ngayReview;

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
