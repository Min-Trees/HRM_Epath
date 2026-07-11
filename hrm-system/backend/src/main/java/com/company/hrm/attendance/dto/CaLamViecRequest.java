package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.CaLamViec.LoaiCa;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalTime;

public class CaLamViecRequest {

    @NotBlank
    @Size(max = 20)
    private String maCa;

    @NotBlank
    @Size(max = 100)
    private String tenCa;

    @NotNull
    private LoaiCa loaiCa;

    @NotNull
    private LocalTime gioBatDau;

    @NotNull
    private LocalTime gioKetThuc;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal soGioChuan;

    private Boolean quaNgay = false;

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public LoaiCa getLoaiCa() { return loaiCa; }
    public void setLoaiCa(LoaiCa loaiCa) { this.loaiCa = loaiCa; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public BigDecimal getSoGioChuan() { return soGioChuan; }
    public void setSoGioChuan(BigDecimal soGioChuan) { this.soGioChuan = soGioChuan; }
    public Boolean getQuaNgay() { return quaNgay; }
    public void setQuaNgay(Boolean quaNgay) { this.quaNgay = quaNgay; }
}
