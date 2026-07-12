package com.company.hrm.training.dto;

import com.company.hrm.training.entity.TrangThaiDangKy;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DangKyDaoTaoDto {
    private UUID id;
    @NotNull
    private UUID lopHocId;
    @NotNull
    private UUID nhanVienId;
    private LocalDateTime ngayDangKy;
    private TrangThaiDangKy trangThai;
    private String lyDoDangKy;
    private UUID nguoiDuyetId;
    private LocalDateTime ngayDuyet;
    private String ghiChuDuyet;
    private BigDecimal diemTongKet;
    private String chungChiCap;
    private LocalDate ngayCap;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getLopHocId() { return lopHocId; }
    public void setLopHocId(UUID v) { this.lopHocId = v; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID v) { this.nhanVienId = v; }
    public LocalDateTime getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDateTime v) { this.ngayDangKy = v; }
    public TrangThaiDangKy getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDangKy v) { this.trangThai = v; }
    public String getLyDoDangKy() { return lyDoDangKy; }
    public void setLyDoDangKy(String v) { this.lyDoDangKy = v; }
    public UUID getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(UUID v) { this.nguoiDuyetId = v; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime v) { this.ngayDuyet = v; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
    public void setGhiChuDuyet(String v) { this.ghiChuDuyet = v; }
    public BigDecimal getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(BigDecimal v) { this.diemTongKet = v; }
    public String getChungChiCap() { return chungChiCap; }
    public void setChungChiCap(String v) { this.chungChiCap = v; }
    public LocalDate getNgayCap() { return ngayCap; }
    public void setNgayCap(LocalDate v) { this.ngayCap = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
