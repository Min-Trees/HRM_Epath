package com.company.hrm.system.dto;

import com.company.hrm.system.entity.Company;
import com.company.hrm.system.entity.TrangThaiCompany;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public class CompanyResponse {

    private UUID companyId;
    private String tenCongTy;
    private String maSoThue;
    private String maSoDkkd;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String nguoiDaiDienPhapLuat;
    private LocalDate ngayDangKy;
    private String goiDichVu;
    private TrangThaiCompany trangThai;
    private Instant createdAt;
    private Instant updatedAt;

    public static CompanyResponse from(Company c) {
        CompanyResponse r = new CompanyResponse();
        r.companyId = c.getCompanyId();
        r.tenCongTy = c.getTenCongTy();
        r.maSoThue = c.getMaSoThue();
        r.maSoDkkd = c.getMaSoDkkd();
        r.diaChi = c.getDiaChi();
        r.soDienThoai = c.getSoDienThoai();
        r.email = c.getEmail();
        r.nguoiDaiDienPhapLuat = c.getNguoiDaiDienPhapLuat();
        r.ngayDangKy = c.getNgayDangKy();
        r.goiDichVu = c.getGoiDichVu();
        r.trangThai = c.getTrangThai();
        r.createdAt = c.getCreatedAt();
        r.updatedAt = c.getUpdatedAt();
        return r;
    }

    public UUID getCompanyId() { return companyId; }
    public String getTenCongTy() { return tenCongTy; }
    public String getMaSoThue() { return maSoThue; }
    public String getMaSoDkkd() { return maSoDkkd; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public String getNguoiDaiDienPhapLuat() { return nguoiDaiDienPhapLuat; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public String getGoiDichVu() { return goiDichVu; }
    public TrangThaiCompany getTrangThai() { return trangThai; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}