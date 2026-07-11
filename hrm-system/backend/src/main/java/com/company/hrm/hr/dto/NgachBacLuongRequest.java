package com.company.hrm.hr.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class NgachBacLuongRequest {
    @NotBlank
    @Size(max = 20)
    private String maNgach;

    @NotBlank
    @Size(max = 200)
    private String tenChucDanh;

    @Min(1)
    private int bacLuong = 1;

    @NotNull
    @Positive
    private BigDecimal heSoLuong;

    private BigDecimal luongCoBanToiThieu;

    public String getMaNgach() { return maNgach; }
    public void setMaNgach(String maNgach) { this.maNgach = maNgach; }
    public String getTenChucDanh() { return tenChucDanh; }
    public void setTenChucDanh(String tenChucDanh) { this.tenChucDanh = tenChucDanh; }
    public int getBacLuong() { return bacLuong; }
    public void setBacLuong(int bacLuong) { this.bacLuong = bacLuong; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { this.heSoLuong = heSoLuong; }
    public BigDecimal getLuongCoBanToiThieu() { return luongCoBanToiThieu; }
    public void setLuongCoBanToiThieu(BigDecimal luongCoBanToiThieu) { this.luongCoBanToiThieu = luongCoBanToiThieu; }
}