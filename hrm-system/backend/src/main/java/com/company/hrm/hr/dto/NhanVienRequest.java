package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.NhanVien.GioiTinh;
import com.company.hrm.hr.entity.NhanVien.TrangThaiNv;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class NhanVienRequest {
    @NotBlank
    @Size(max = 200)
    private String hoTen;

    @Past
    private LocalDate ngaySinh;

    private GioiTinh gioiTinh;

    @Size(max = 20)
    private String soCccd;

    private LocalDate ngayCapCccd;
    private String noiCapCccd;
    private String queQuan;
    private String diaChiThuongTru;
    private String diaChiLienLac;
    private String soDienThoai;
    private String email;
    private String trinhDoHocVan;

    @NotNull
    private LocalDate ngayVaoLam;

    @NotNull
    private UUID phongBanId;

    private UUID ngachBacId;
    private UUID quanLyTrucTiepId;

    /** Mặc định tạo với UNG_VIEN; nếu ký HĐ thử việc ngay thì truyền THU_VIEC. */
    private TrangThaiNv trangThai;

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public GioiTinh getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(GioiTinh gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getSoCccd() { return soCccd; }
    public void setSoCccd(String soCccd) { this.soCccd = soCccd; }
    public LocalDate getNgayCapCccd() { return ngayCapCccd; }
    public void setNgayCapCccd(LocalDate ngayCapCccd) { this.ngayCapCccd = ngayCapCccd; }
    public String getNoiCapCccd() { return noiCapCccd; }
    public void setNoiCapCccd(String noiCapCccd) { this.noiCapCccd = noiCapCccd; }
    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
    public String getDiaChiThuongTru() { return diaChiThuongTru; }
    public void setDiaChiThuongTru(String diaChiThuongTru) { this.diaChiThuongTru = diaChiThuongTru; }
    public String getDiaChiLienLac() { return diaChiLienLac; }
    public void setDiaChiLienLac(String diaChiLienLac) { this.diaChiLienLac = diaChiLienLac; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTrinhDoHocVan() { return trinhDoHocVan; }
    public void setTrinhDoHocVan(String trinhDoHocVan) { this.trinhDoHocVan = trinhDoHocVan; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public UUID getNgachBacId() { return ngachBacId; }
    public void setNgachBacId(UUID ngachBacId) { this.ngachBacId = ngachBacId; }
    public UUID getQuanLyTrucTiepId() { return quanLyTrucTiepId; }
    public void setQuanLyTrucTiepId(UUID quanLyTrucTiepId) { this.quanLyTrucTiepId = quanLyTrucTiepId; }
    public TrangThaiNv getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNv trangThai) { this.trangThai = trangThai; }
}