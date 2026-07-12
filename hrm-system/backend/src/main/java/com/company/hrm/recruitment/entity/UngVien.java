package com.company.hrm.recruitment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ung_vien", schema = "recruitment")
public class UngVien {

    @Id
    @GeneratedValue
    @Column(name = "ung_vien_id")
    private UUID ungVienId;

    @Column(name = "ma_ung_vien", length = 20, unique = true)
    private String maUngVien;

    @Column(name = "ho_ten", length = 200, nullable = false)
    private String hoTen;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "dia_chi", length = 300)
    private String diaChi;

    @Column(name = "cmnd", length = 20)
    private String cmnd;

    @Column(name = "ngay_cap_cmnd")
    private LocalDate ngayCapCmnd;

    @Column(name = "noi_cap_cmnd", length = 200)
    private String noiCapCmnd;

    @Column(name = "trinh_do", length = 100)
    private String trinhDo;

    @Column(name = "truong_dao_tao", length = 300)
    private String truongDaoTao;

    @Column(name = "chuyen_nganh", length = 200)
    private String chuyenNganh;

    @Column(name = "nam_tot_nghiep")
    private Integer namTotNghiep;

    @Column(name = "so_nam_kinh_nghiem")
    private Integer soNamKinhNghiem;

    @Column(name = "cong_ty_cu", length = 300)
    private String congTyCu;

    @Column(name = "chuc_danh_cu", length = 200)
    private String chucDanhCu;

    @Column(name = "cv_url", length = 500)
    private String cvUrl;

    @Column(name = "thu_xin_viec_url", length = 500)
    private String thuXinViecUrl;

    @Column(name = "ky_nang", columnDefinition = "TEXT")
    private String kyNang;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "recruitment.trang_thai_ung_vien")
    private TrangThaiUngVien trangThai = TrangThaiUngVien.MOI_NOP_HO_SO;

    @Column(name = "yeu_cau_id")
    private UUID yeuCauId;

    @Column(name = "nguoi_gioi_thieu_id")
    private UUID nguoiGioiThieuId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

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
