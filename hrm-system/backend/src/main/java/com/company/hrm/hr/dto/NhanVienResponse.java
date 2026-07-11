package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.NhanVien;
import com.company.hrm.hr.entity.NhanVien.GioiTinh;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class NhanVienResponse {
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;
    private LocalDate ngaySinh;
    private GioiTinh gioiTinh;
    private String soCccd;
    private LocalDate ngayCapCccd;
    private String noiCapCccd;
    private String queQuan;
    private String diaChiThuongTru;
    private String diaChiLienLac;
    private String soDienThoai;
    private String email;
    private String trinhDoHocVan;
    private LocalDate ngayVaoLam;
    private UUID phongBanId;
    private UUID ngachBacId;
    private UUID quanLyTrucTiepId;
    private TrangThaiNv trangThai;
    private String taiKhoanChamCongId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<NguoiPhuThuocResponse> dependents;
    private List<QuaTrinhCongTacResponse> workHistory;

    public static NhanVienResponse from(NhanVien e) {
        NhanVienResponse r = new NhanVienResponse();
        r.nhanVienId = e.getNhanVienId();
        r.maNv = e.getMaNv();
        r.hoTen = e.getHoTen();
        r.ngaySinh = e.getNgaySinh();
        r.gioiTinh = e.getGioiTinh();
        r.soCccd = e.getSoCccd();
        r.ngayCapCccd = e.getNgayCapCccd();
        r.noiCapCccd = e.getNoiCapCccd();
        r.queQuan = e.getQueQuan();
        r.diaChiThuongTru = e.getDiaChiThuongTru();
        r.diaChiLienLac = e.getDiaChiLienLac();
        r.soDienThoai = e.getSoDienThoai();
        r.email = e.getEmail();
        r.trinhDoHocVan = e.getTrinhDoHocVan();
        r.ngayVaoLam = e.getNgayVaoLam();
        r.phongBanId = e.getPhongBanId();
        r.ngachBacId = e.getNgachBacId();
        r.quanLyTrucTiepId = e.getQuanLyTrucTiepId();
        r.trangThai = e.getTrangThai();
        r.taiKhoanChamCongId = e.getTaiKhoanChamCongId();
        r.createdAt = e.getCreatedAt();
        r.updatedAt = e.getUpdatedAt();
        return r;
    }

    public UUID getNhanVienId() { return nhanVienId; }
    public String getMaNv() { return maNv; }
    public String getHoTen() { return hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public GioiTinh getGioiTinh() { return gioiTinh; }
    public String getSoCccd() { return soCccd; }
    public LocalDate getNgayCapCccd() { return ngayCapCccd; }
    public String getNoiCapCccd() { return noiCapCccd; }
    public String getQueQuan() { return queQuan; }
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
    public String getDiaChiLienLac() { return diaChiLienLac; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getEmail() { return email; }
    public String getTrinhDoHocVan() { return trinhDoHocVan; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public UUID getPhongBanId() { return phongBanId; }
    public UUID getNgachBacId() { return ngachBacId; }
    public UUID getQuanLyTrucTiepId() { return quanLyTrucTiepId; }
    public TrangThaiNv getTrangThai() { return trangThai; }
    public String getTaiKhoanChamCongId() { return taiKhoanChamCongId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<NguoiPhuThuocResponse> getDependents() { return dependents; }
    public void setDependents(List<NguoiPhuThuocResponse> dependents) { this.dependents = dependents; }
    public List<QuaTrinhCongTacResponse> getWorkHistory() { return workHistory; }
    public void setWorkHistory(List<QuaTrinhCongTacResponse> workHistory) { this.workHistory = workHistory; }
}