package com.company.hrm.training.dto;

import com.company.hrm.training.entity.TrangThaiThamDu;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DiemDanhDto {
    private UUID id;
    @NotNull
    private UUID dangKyId;
    @NotNull @Min(1)
    private Integer buoiSo;
    @NotNull
    private LocalDate ngayHoc;
    private TrangThaiThamDu trangThai;
    @DecimalMin("0.0") @DecimalMax("10.0")
    private BigDecimal diemBaiTap;
    private String ghiChu;
    private UUID nguoiDiemDanhId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getDangKyId() { return dangKyId; }
    public void setDangKyId(UUID v) { this.dangKyId = v; }
    public Integer getBuoiSo() { return buoiSo; }
    public void setBuoiSo(Integer v) { this.buoiSo = v; }
    public LocalDate getNgayHoc() { return ngayHoc; }
    public void setNgayHoc(LocalDate v) { this.ngayHoc = v; }
    public TrangThaiThamDu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiThamDu v) { this.trangThai = v; }
    public BigDecimal getDiemBaiTap() { return diemBaiTap; }
    public void setDiemBaiTap(BigDecimal v) { this.diemBaiTap = v; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String v) { this.ghiChu = v; }
    public UUID getNguoiDiemDanhId() { return nguoiDiemDanhId; }
    public void setNguoiDiemDanhId(UUID v) { this.nguoiDiemDanhId = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
