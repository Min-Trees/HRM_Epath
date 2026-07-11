package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.NguoiPhuThuoc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class NguoiPhuThuocResponse {
    private UUID nguoiPhuThuocId;
    private UUID nhanVienId;
    private String hoTen;
    private LocalDate ngaySinh;
    private String quanHe;
    private String soCccdHoacKhaiSinh;
    private String maSoThuePhuThuoc;
    private LocalDate tuNgayGiamTru;
    private LocalDate denNgayGiamTru;
    private boolean active;
    private Instant createdAt;

    public static NguoiPhuThuocResponse from(NguoiPhuThuoc e) {
        NguoiPhuThuocResponse r = new NguoiPhuThuocResponse();
        r.nguoiPhuThuocId = e.getNguoiPhuThuocId();
        r.nhanVienId = e.getNhanVienId();
        r.hoTen = e.getHoTen();
        r.ngaySinh = e.getNgaySinh();
        r.quanHe = e.getQuanHe();
        r.soCccdHoacKhaiSinh = e.getSoCccdHoacKhaiSinh();
        r.maSoThuePhuThuoc = e.getMaSoThuePhuThuoc();
        r.tuNgayGiamTru = e.getTuNgayGiamTru();
        r.denNgayGiamTru = e.getDenNgayGiamTru();
        r.active = e.isActive();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getNguoiPhuThuocId() { return nguoiPhuThuocId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public String getHoTen() { return hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public String getQuanHe() { return quanHe; }
    public String getSoCccdHoacKhaiSinh() { return soCccdHoacKhaiSinh; }
    public String getMaSoThuePhuThuoc() { return maSoThuePhuThuoc; }
    public LocalDate getTuNgayGiamTru() { return tuNgayGiamTru; }
    public LocalDate getDenNgayGiamTru() { return denNgayGiamTru; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}