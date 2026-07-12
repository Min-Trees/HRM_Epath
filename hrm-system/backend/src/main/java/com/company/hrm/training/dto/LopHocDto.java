package com.company.hrm.training.dto;

import com.company.hrm.training.entity.TrangThaiLop;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LopHocDto {
    private UUID id;
    @NotBlank
    private String maLop;
    @NotNull
    private UUID chuongTrinhId;
    @NotBlank
    private String tenLop;
    @NotNull
    private LocalDate ngayBatDau;
    @NotNull
    private LocalDate ngayKetThuc;
    @NotNull @Min(1)
    private Integer soBuoi;
    @Min(1)
    private Integer soChoToiDa = 30;
    private String diaDiem;
    private String giangVien;
    private BigDecimal chiPhiMoiNv = BigDecimal.ZERO;
    private TrangThaiLop trangThai = TrangThaiLop.MO_DANG_KY;
    private String ghiChu;
    private UUID nguoiPhuTrachId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public String getMaLop() { return maLop; }
    public void setMaLop(String v) { this.maLop = v; }
    public UUID getChuongTrinhId() { return chuongTrinhId; }
    public void setChuongTrinhId(UUID v) { this.chuongTrinhId = v; }
    public String getTenLop() { return tenLop; }
    public void setTenLop(String v) { this.tenLop = v; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate v) { this.ngayBatDau = v; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate v) { this.ngayKetThuc = v; }
    public Integer getSoBuoi() { return soBuoi; }
    public void setSoBuoi(Integer v) { this.soBuoi = v; }
    public Integer getSoChoToiDa() { return soChoToiDa; }
    public void setSoChoToiDa(Integer v) { this.soChoToiDa = v; }
    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String v) { this.diaDiem = v; }
    public String getGiangVien() { return giangVien; }
    public void setGiangVien(String v) { this.giangVien = v; }
    public BigDecimal getChiPhiMoiNv() { return chiPhiMoiNv; }
    public void setChiPhiMoiNv(BigDecimal v) { this.chiPhiMoiNv = v; }
    public TrangThaiLop getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLop v) { this.trangThai = v; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String v) { this.ghiChu = v; }
    public UUID getNguoiPhuTrachId() { return nguoiPhuTrachId; }
    public void setNguoiPhuTrachId(UUID v) { this.nguoiPhuTrachId = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
