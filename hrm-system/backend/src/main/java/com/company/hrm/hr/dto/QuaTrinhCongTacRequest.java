package com.company.hrm.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class QuaTrinhCongTacRequest {
    @NotBlank
    private String donVi;

    private String chucDanh;

    @NotNull
    private LocalDate tuNgay;

    private LocalDate denNgay;
    private String moTa;

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }
    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}