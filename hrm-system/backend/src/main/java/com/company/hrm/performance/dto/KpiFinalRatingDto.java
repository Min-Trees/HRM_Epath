package com.company.hrm.performance.dto;

import com.company.hrm.performance.entity.XepLoai;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class KpiFinalRatingDto {

    private UUID finalRatingId;

    @NotNull
    private UUID assignmentId;

    @NotNull
    private UUID nguoiPheDuyetId;

    @NotNull
    private XepLoai xepLoaiCuoi;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemCuoi;

    private String nhanXetHr;
    private BigDecimal heSoThuong;
    private LocalDateTime ngayPheDuyet;

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
