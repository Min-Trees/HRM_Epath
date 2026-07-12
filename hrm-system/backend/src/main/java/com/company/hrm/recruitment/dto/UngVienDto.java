package com.company.hrm.recruitment.dto;

import com.company.hrm.recruitment.entity.TrangThaiUngVien;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UngVienDto {

    private UUID ungVienId;
    private String maUngVien;

    @NotBlank
    @Size(max = 200)
    private String hoTen;

    private LocalDate ngaySinh;
    private String gioiTinh;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String cmnd;
    private LocalDate ngayCapCmnd;
    private String noiCapCmnd;
    private String trinhDo;
    private String truongDaoTao;
    private String chuyenNganh;
    private Integer namTotNghiep;
    private Integer soNamKinhNghiem;
    private String congTyCu;
    private String chucDanhCu;
    private String cvUrl;
    private String thuXinViecUrl;
    private String kyNang;
    private String ghiChu;
    private TrangThaiUngVien trangThai;
    private UUID yeuCauId;
    private UUID nguoiGioiThieuId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getUngVienId() { return ungVienId; }
    public void setUngVienId(UUID ungVienId) { this.ungVienId = ungVienId; }
    public String getMaUngVien() { return maUngVien; }
    public void setMaUngVien(String maUngVien) { this.maUngVien = maUngVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getCmnd() { return cmnd; }
    public void setCmnd(String cmnd) { this.cmnd = cmnd; }
    public LocalDate getNgayCapCmnd() { return ngayCapCmnd; }
    public void setNgayCapCmnd(LocalDate ngayCapCmnd) { this.ngayCapCmnd = ngayCapCmnd; }
    public String getNoiCapCmnd() { return noiCapCmnd; }
    public void setNoiCapCmnd(String noiCapCmnd) { this.noiCapCmnd = noiCapCmnd; }
    public String getTrinhDo() { return trinhDo; }
    public void setTrinhDo(String trinhDo) { this.trinhDo = trinhDo; }
    public String getTruongDaoTao() { return truongDaoTao; }
    public void setTruongDaoTao(String truongDaoTao) { this.truongDaoTao = truongDaoTao; }
    public String getChuyenNganh() { return chuyenNganh; }
    public void setChuyenNganh(String chuyenNganh) { this.chuyenNganh = chuyenNganh; }
    public Integer getNamTotNghiep() { return namTotNghiep; }
    public void setNamTotNghiep(Integer namTotNghiep) { this.namTotNghiep = namTotNghiep; }
    public Integer getSoNamKinhNghiem() { return soNamKinhNghiem; }
    public void setSoNamKinhNghiem(Integer soNamKinhNghiem) { this.soNamKinhNghiem = soNamKinhNghiem; }
    public String getCongTyCu() { return congTyCu; }
    public void setCongTyCu(String congTyCu) { this.congTyCu = congTyCu; }
    public String getChucDanhCu() { return chucDanhCu; }
    public void setChucDanhCu(String chucDanhCu) { this.chucDanhCu = chucDanhCu; }
    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
    public String getThuXinViecUrl() { return thuXinViecUrl; }
    public void setThuXinViecUrl(String thuXinViecUrl) { this.thuXinViecUrl = thuXinViecUrl; }
    public String getKyNang() { return kyNang; }
    public void setKyNang(String kyNang) { this.kyNang = kyNang; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public TrangThaiUngVien getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiUngVien trangThai) { this.trangThai = trangThai; }
    public UUID getYeuCauId() { return yeuCauId; }
    public void setYeuCauId(UUID yeuCauId) { this.yeuCauId = yeuCauId; }
    public UUID getNguoiGioiThieuId() { return nguoiGioiThieuId; }
    public void setNguoiGioiThieuId(UUID nguoiGioiThieuId) { this.nguoiGioiThieuId = nguoiGioiThieuId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
