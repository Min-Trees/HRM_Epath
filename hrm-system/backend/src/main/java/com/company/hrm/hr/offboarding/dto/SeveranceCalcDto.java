package com.company.hrm.hr.offboarding.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO tra ve ket qua tinh tro cap thoi viec.
 *
 * <p>Neu {@link #caseId} null -> chi tinh toan (preview mode) khong luu DB.
 * Neu co caseId -> service se luu vao SeveranceCalc.
 */
public class SeveranceCalcDto {

    private UUID severanceId;
    private UUID caseId;

    @NotNull
    private UUID nhanVienId;

    @NotNull
    private Integer thoiGianLamViecThang;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal soNamThamNien;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal luongThoiViecBinhQuan;

    @NotNull
    private BigDecimal heSo;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal soTienTroCap;

    private String ghiChu;
    private UUID nguoiTinhId;
    private LocalDateTime ngayTinh;

    private String hoTen;
    private String maNv;
    private java.time.LocalDate ngayVaoLam;
    private java.time.LocalDate ngayNghiViecCuoi;

    public UUID getSeveranceId() { return severanceId; }
    public void setSeveranceId(UUID severanceId) { this.severanceId = severanceId; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public Integer getThoiGianLamViecThang() { return thoiGianLamViecThang; }
    public void setThoiGianLamViecThang(Integer thoiGianLamViecThang) { this.thoiGianLamViecThang = thoiGianLamViecThang; }
    public BigDecimal getSoNamThamNien() { return soNamThamNien; }
    public void setSoNamThamNien(BigDecimal soNamThamNien) { this.soNamThamNien = soNamThamNien; }
    public BigDecimal getLuongThoiViecBinhQuan() { return luongThoiViecBinhQuan; }
    public void setLuongThoiViecBinhQuan(BigDecimal luongThoiViecBinhQuan) { this.luongThoiViecBinhQuan = luongThoiViecBinhQuan; }
    public BigDecimal getHeSo() { return heSo; }
    public void setHeSo(BigDecimal heSo) { this.heSo = heSo; }
    public BigDecimal getSoTienTroCap() { return soTienTroCap; }
    public void setSoTienTroCap(BigDecimal soTienTroCap) { this.soTienTroCap = soTienTroCap; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiTinhId() { return nguoiTinhId; }
    public void setNguoiTinhId(UUID nguoiTinhId) { this.nguoiTinhId = nguoiTinhId; }
    public LocalDateTime getNgayTinh() { return ngayTinh; }
    public void setNgayTinh(LocalDateTime ngayTinh) { this.ngayTinh = ngayTinh; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public java.time.LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(java.time.LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public java.time.LocalDate getNgayNghiViecCuoi() { return ngayNghiViecCuoi; }
    public void setNgayNghiViecCuoi(java.time.LocalDate ngayNghiViecCuoi) { this.ngayNghiViecCuoi = ngayNghiViecCuoi; }
}
