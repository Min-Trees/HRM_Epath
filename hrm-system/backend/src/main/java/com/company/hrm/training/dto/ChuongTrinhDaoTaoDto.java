package com.company.hrm.training.dto;

import com.company.hrm.training.entity.LoaiChuongTrinh;
import com.company.hrm.training.entity.TrangThaiChuongTrinh;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChuongTrinhDaoTaoDto {
    private UUID id;
    @NotBlank
    private String maChuongTrinh;
    @NotBlank
    private String tenChuongTrinh;
    @NotNull
    private LoaiChuongTrinh loaiChuongTrinh;
    private String moTa;
    private String mucTieu;
    @NotNull
    @DecimalMin("0.5")
    private BigDecimal thoiLuongGio;
    private BigDecimal diemDanhGiaToiThieu = new BigDecimal("60.0");
    private String chungChi;
    private TrangThaiChuongTrinh trangThai = TrangThaiChuongTrinh.NHAP;
    private UUID nguoiTaoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMaChuongTrinh() { return maChuongTrinh; }
    public void setMaChuongTrinh(String s) { this.maChuongTrinh = s; }
    public String getTenChuongTrinh() { return tenChuongTrinh; }
    public void setTenChuongTrinh(String s) { this.tenChuongTrinh = s; }
    public LoaiChuongTrinh getLoaiChuongTrinh() { return loaiChuongTrinh; }
    public void setLoaiChuongTrinh(LoaiChuongTrinh l) { this.loaiChuongTrinh = l; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String s) { this.moTa = s; }
    public String getMucTieu() { return mucTieu; }
    public void setMucTieu(String s) { this.mucTieu = s; }
    public BigDecimal getThoiLuongGio() { return thoiLuongGio; }
    public void setThoiLuongGio(BigDecimal v) { this.thoiLuongGio = v; }
    public BigDecimal getDiemDanhGiaToiThieu() { return diemDanhGiaToiThieu; }
    public void setDiemDanhGiaToiThieu(BigDecimal v) { this.diemDanhGiaToiThieu = v; }
    public String getChungChi() { return chungChi; }
    public void setChungChi(String s) { this.chungChi = s; }
    public TrangThaiChuongTrinh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiChuongTrinh t) { this.trangThai = t; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID v) { this.nguoiTaoId = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
