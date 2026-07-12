package com.company.hrm.payroll.run.dto;

import com.company.hrm.payroll.run.entity.LoaiKhoanLuong;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public class KhoanLuongDto {

    private UUID khoanId;
    private UUID bangLuongId;
    private UUID kyLinhId;

    @NotNull
    private LoaiKhoanLuong loaiKhoan;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal soTien;

    private String moTa;
    private UUID nguoiThemId;

    public UUID getKhoanId() { return khoanId; }
    public void setKhoanId(UUID khoanId) { this.khoanId = khoanId; }
    public UUID getBangLuongId() { return bangLuongId; }
    public void setBangLuongId(UUID bangLuongId) { this.bangLuongId = bangLuongId; }
    public UUID getKyLinhId() { return kyLinhId; }
    public void setKyLinhId(UUID kyLinhId) { this.kyLinhId = kyLinhId; }
    public LoaiKhoanLuong getLoaiKhoan() { return loaiKhoan; }
    public void setLoaiKhoan(LoaiKhoanLuong loaiKhoan) { this.loaiKhoan = loaiKhoan; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public UUID getNguoiThemId() { return nguoiThemId; }
    public void setNguoiThemId(UUID nguoiThemId) { this.nguoiThemId = nguoiThemId; }
}
