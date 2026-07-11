package com.company.hrm.hr.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class PhongBanRequest {
    @NotBlank
    @Size(max = 20)
    private String maPhongBan;

    @NotBlank
    @Size(max = 200)
    private String tenPhongBan;

    private UUID phongBanChaId;

    @Min(0)
    private int dinhBien = 0;

    private UUID truongBoPhanId;

    public String getMaPhongBan() { return maPhongBan; }
    public void setMaPhongBan(String maPhongBan) { this.maPhongBan = maPhongBan; }
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
    public UUID getPhongBanChaId() { return phongBanChaId; }
    public void setPhongBanChaId(UUID phongBanChaId) { this.phongBanChaId = phongBanChaId; }
    public int getDinhBien() { return dinhBien; }
    public void setDinhBien(int dinhBien) { this.dinhBien = dinhBien; }
    public UUID getTruongBoPhanId() { return truongBoPhanId; }
    public void setTruongBoPhanId(UUID truongBoPhanId) { this.truongBoPhanId = truongBoPhanId; }
}