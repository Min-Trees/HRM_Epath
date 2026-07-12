package com.company.hrm.payroll.run.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T19 - Ky linh luong (payroll run).
 *
 * <p>Workflow:
 * <pre>
 *   CHO_CHAY -> DANG_CHAY -> DA_CHAY -> DA_DUYET_CAP_1 -> DA_DUYET_CAP_2 -> DA_CHI_TRA
 *   (co the HUY o bat ky buoc nao truoc DA_CHI_TRA)
 * </pre>
 */
@Entity
@Table(name = "ky_linh_luong", schema = "payroll")
public class KyLinhLuong {

    @Id
    @GeneratedValue
    @Column(name = "ky_linh_id")
    private UUID kyLinhId;

    @Column(name = "ma_ky_linh", length = 20, unique = true)
    private String maKyLinh;

    @Column(name = "thang", nullable = false)
    private Integer thang;

    @Column(name = "nam", nullable = false)
    private Integer nam;

    @Column(name = "ngay_chot_cong")
    private LocalDate ngayChotCong;

    @Column(name = "ngay_chi_tra")
    private LocalDate ngayChiTra;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "payroll.trang_thai_ky_luong")
    private TrangThaiKyLuong trangThai = TrangThaiKyLuong.CHO_CHAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_hinh_chi_tra", length = 30)
    private LoaiHinhChuyen loaiHinhChiTra = LoaiHinhChuyen.BANK_TRANSFER;

    @Column(name = "tong_nhan_vien")
    private Integer tongNhanVien = 0;

    @Column(name = "tong_thuc_linh", precision = 18, scale = 2)
    private BigDecimal tongThucLinh = BigDecimal.ZERO;

    @Column(name = "tong_bhxh_nld", precision = 18, scale = 2)
    private BigDecimal tongBhxhNld = BigDecimal.ZERO;

    @Column(name = "tong_thue_tncn", precision = 18, scale = 2)
    private BigDecimal tongThueTncn = BigDecimal.ZERO;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "nguoi_chay_id")
    private UUID nguoiChayId;

    @Column(name = "nguoi_duyet_cap_1_id")
    private UUID nguoiDuyetCap1Id;

    @Column(name = "nguoi_duyet_cap_2_id")
    private UUID nguoiDuyetCap2Id;

    @Column(name = "ngay_chay")
    private LocalDateTime ngayChay;

    @Column(name = "ngay_duyet_cap_1")
    private LocalDateTime ngayDuyetCap1;

    @Column(name = "ngay_duyet_cap_2")
    private LocalDateTime ngayDuyetCap2;

    @Column(name = "ngay_chi_tra_thuc_te")
    private LocalDateTime ngayChiTraThucTe;

    @Column(name = "file_zip_url", length = 500)
    private String fileZipUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getKyLinhId() { return kyLinhId; }
    public void setKyLinhId(UUID kyLinhId) { this.kyLinhId = kyLinhId; }
    public String getMaKyLinh() { return maKyLinh; }
    public void setMaKyLinh(String maKyLinh) { this.maKyLinh = maKyLinh; }
    public Integer getThang() { return thang; }
    public void setThang(Integer thang) { this.thang = thang; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public LocalDate getNgayChotCong() { return ngayChotCong; }
    public void setNgayChotCong(LocalDate ngayChotCong) { this.ngayChotCong = ngayChotCong; }
    public LocalDate getNgayChiTra() { return ngayChiTra; }
    public void setNgayChiTra(LocalDate ngayChiTra) { this.ngayChiTra = ngayChiTra; }
    public TrangThaiKyLuong getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiKyLuong trangThai) { this.trangThai = trangThai; }
    public LoaiHinhChuyen getLoaiHinhChiTra() { return loaiHinhChiTra; }
    public void setLoaiHinhChiTra(LoaiHinhChuyen loaiHinhChiTra) { this.loaiHinhChiTra = loaiHinhChiTra; }
    public Integer getTongNhanVien() { return tongNhanVien; }
    public void setTongNhanVien(Integer tongNhanVien) { this.tongNhanVien = tongNhanVien; }
    public BigDecimal getTongThucLinh() { return tongThucLinh; }
    public void setTongThucLinh(BigDecimal tongThucLinh) { this.tongThucLinh = tongThucLinh; }
    public BigDecimal getTongBhxhNld() { return tongBhxhNld; }
    public void setTongBhxhNld(BigDecimal tongBhxhNld) { this.tongBhxhNld = tongBhxhNld; }
    public BigDecimal getTongThueTncn() { return tongThueTncn; }
    public void setTongThueTncn(BigDecimal tongThueTncn) { this.tongThueTncn = tongThueTncn; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiChayId() { return nguoiChayId; }
    public void setNguoiChayId(UUID nguoiChayId) { this.nguoiChayId = nguoiChayId; }
    public UUID getNguoiDuyetCap1Id() { return nguoiDuyetCap1Id; }
    public void setNguoiDuyetCap1Id(UUID nguoiDuyetCap1Id) { this.nguoiDuyetCap1Id = nguoiDuyetCap1Id; }
    public UUID getNguoiDuyetCap2Id() { return nguoiDuyetCap2Id; }
    public void setNguoiDuyetCap2Id(UUID nguoiDuyetCap2Id) { this.nguoiDuyetCap2Id = nguoiDuyetCap2Id; }
    public LocalDateTime getNgayChay() { return ngayChay; }
    public void setNgayChay(LocalDateTime ngayChay) { this.ngayChay = ngayChay; }
    public LocalDateTime getNgayDuyetCap1() { return ngayDuyetCap1; }
    public void setNgayDuyetCap1(LocalDateTime ngayDuyetCap1) { this.ngayDuyetCap1 = ngayDuyetCap1; }
    public LocalDateTime getNgayDuyetCap2() { return ngayDuyetCap2; }
    public void setNgayDuyetCap2(LocalDateTime ngayDuyetCap2) { this.ngayDuyetCap2 = ngayDuyetCap2; }
    public LocalDateTime getNgayChiTraThucTe() { return ngayChiTraThucTe; }
    public void setNgayChiTraThucTe(LocalDateTime ngayChiTraThucTe) { this.ngayChiTraThucTe = ngayChiTraThucTe; }
    public String getFileZipUrl() { return fileZipUrl; }
    public void setFileZipUrl(String fileZipUrl) { this.fileZipUrl = fileZipUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
