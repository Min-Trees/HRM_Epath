package com.company.hrm.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class NguoiPhuThuocRequest {
    @NotBlank
    private String hoTen;

    private LocalDate ngaySinh;

    @NotBlank
    private String quanHe;

    private String soCccdHoacKhaiSinh;
    private String maSoThuePhuThuoc;

    @NotNull
    private LocalDate tuNgayGiamTru;

    private LocalDate denNgayGiamTru;

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getQuanHe() { return quanHe; }
    public void setQuanHe(String quanHe) { this.quanHe = quanHe; }
    public String getSoCccdHoacKhaiSinh() { return soCccdHoacKhaiSinh; }
    public void setSoCccdHoacKhaiSinh(String soCccdHoacKhaiSinh) { this.soCccdHoacKhaiSinh = soCccdHoacKhaiSinh; }
    public String getMaSoThuePhuThuoc() { return maSoThuePhuThuoc; }
    public void setMaSoThuePhuThuoc(String maSoThuePhuThuoc) { this.maSoThuePhuThuoc = maSoThuePhuThuoc; }
    public LocalDate getTuNgayGiamTru() { return tuNgayGiamTru; }
    public void setTuNgayGiamTru(LocalDate tuNgayGiamTru) { this.tuNgayGiamTru = tuNgayGiamTru; }
    public LocalDate getDenNgayGiamTru() { return denNgayGiamTru; }
    public void setDenNgayGiamTru(LocalDate denNgayGiamTru) { this.denNgayGiamTru = denNgayGiamTru; }
}