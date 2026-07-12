package com.company.hrm.payroll.tax.dto;

import com.company.hrm.payroll.tax.entity.LoaiCamKet08;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class CamKet08Dto {

    private UUID camKetId;
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;

    @NotNull
    private Integer nam;

    @NotNull
    private LoaiCamKet08 loaiCamKet;

    @NotNull
    private LocalDate ngayDangKy;

    @NotNull
    private LocalDate hieuLucTuNgay;

    private LocalDate hieuLucDenNgay;
    private Boolean uyQuyenQtt;
    private String ghiChu;

    public UUID getCamKetId() { return camKetId; }
    public void setCamKetId(UUID camKetId) { this.camKetId = camKetId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public LoaiCamKet08 getLoaiCamKet() { return loaiCamKet; }
    public void setLoaiCamKet(LoaiCamKet08 loaiCamKet) { this.loaiCamKet = loaiCamKet; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public LocalDate getHieuLucTuNgay() { return hieuLucTuNgay; }
    public void setHieuLucTuNgay(LocalDate hieuLucTuNgay) { this.hieuLucTuNgay = hieuLucTuNgay; }
    public LocalDate getHieuLucDenNgay() { return hieuLucDenNgay; }
    public void setHieuLucDenNgay(LocalDate hieuLucDenNgay) { this.hieuLucDenNgay = hieuLucDenNgay; }
    public Boolean getUyQuyenQtt() { return uyQuyenQtt; }
    public void setUyQuyenQtt(Boolean uyQuyenQtt) { this.uyQuyenQtt = uyQuyenQtt; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
