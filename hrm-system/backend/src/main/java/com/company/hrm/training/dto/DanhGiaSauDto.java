package com.company.hrm.training.dto;

import com.company.hrm.training.entity.KetQuaDanhGia;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DanhGiaSauDto {
    private UUID id;
    @NotNull
    private UUID dangKyId;
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemNoiDung;
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemGiangVien;
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal diemThucHanh;
    /** Generated column - read-only from DB */
    private BigDecimal diemTrungBinh;
    private KetQuaDanhGia ketQua;
    private String yKienNguoiHoc;
    private String yKienGv;
    private LocalDateTime ngayDanhGia;
    private UUID nguoiDanhGiaId;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public UUID getDangKyId() { return dangKyId; }
    public void setDangKyId(UUID v) { this.dangKyId = v; }
    public BigDecimal getDiemNoiDung() { return diemNoiDung; }
    public void setDiemNoiDung(BigDecimal v) { this.diemNoiDung = v; }
    public BigDecimal getDiemGiangVien() { return diemGiangVien; }
    public void setDiemGiangVien(BigDecimal v) { this.diemGiangVien = v; }
    public BigDecimal getDiemThucHanh() { return diemThucHanh; }
    public void setDiemThucHanh(BigDecimal v) { this.diemThucHanh = v; }
    public BigDecimal getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(BigDecimal v) { this.diemTrungBinh = v; }
    public KetQuaDanhGia getKetQua() { return ketQua; }
    public void setKetQua(KetQuaDanhGia v) { this.ketQua = v; }
    public String getYKienNguoiHoc() { return yKienNguoiHoc; }
    public void setYKienNguoiHoc(String v) { this.yKienNguoiHoc = v; }
    public String getYKienGv() { return yKienGv; }
    public void setYKienGv(String v) { this.yKienGv = v; }
    public LocalDateTime getNgayDanhGia() { return ngayDanhGia; }
    public void setNgayDanhGia(LocalDateTime v) { this.ngayDanhGia = v; }
    public UUID getNguoiDanhGiaId() { return nguoiDanhGiaId; }
    public void setNguoiDanhGiaId(UUID v) { this.nguoiDanhGiaId = v; }
}
