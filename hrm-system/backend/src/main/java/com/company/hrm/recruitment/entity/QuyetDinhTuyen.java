package com.company.hrm.recruitment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quyet_dinh_tuyen", schema = "recruitment")
public class QuyetDinhTuyen {

    @Id
    @GeneratedValue
    @Column(name = "quyet_dinh_id")
    private UUID quyetDinhId;

    @Column(name = "ung_vien_id", nullable = false)
    private UUID ungVienId;

    @Column(name = "nguoi_quyet_dinh_id", nullable = false)
    private UUID nguoiQuyetDinhId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai_hop_dong", nullable = false, columnDefinition = "recruitment.loai_hop_dong_de_nghi")
    private LoaiHopDongDeNghi loaiHopDong;

    @Column(name = "muc_luong_de_nghi", precision = 14, scale = 2, nullable = false)
    private BigDecimal mucLuongDeNghi;

    @Column(name = "ngay_vao_lam_de_nghi", nullable = false)
    private LocalDate ngayVaoLamDeNghi;

    @Column(name = "phong_ban_id", nullable = false)
    private UUID phongBanId;

    @Column(name = "chuc_danh", length = 200)
    private String chucDanh;

    @Column(name = "thoi_han_thu_viec_thang")
    private Integer thoiHanThuViecThang;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 30, nullable = false)
    private TrangThaiQuyetDinh trangThai = TrangThaiQuyetDinh.CHO_PHAN_HOI;

    @Column(name = "ngay_ung_vien_phan_hoi")
    private LocalDate ngayUngVienPhanHoi;

    @Column(name = "nhan_vien_moi_id")
    private UUID nhanVienMoiId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getQuyetDinhId() { return quyetDinhId; }
    public void setQuyetDinhId(UUID quyetDinhId) { this.quyetDinhId = quyetDinhId; }
    public UUID getUngVienId() { return ungVienId; }
    public void setUngVienId(UUID ungVienId) { this.ungVienId = ungVienId; }
    public UUID getNguoiQuyetDinhId() { return nguoiQuyetDinhId; }
    public void setNguoiQuyetDinhId(UUID nguoiQuyetDinhId) { this.nguoiQuyetDinhId = nguoiQuyetDinhId; }
    public LoaiHopDongDeNghi getLoaiHopDong() { return loaiHopDong; }
    public void setLoaiHopDong(LoaiHopDongDeNghi loaiHopDong) { this.loaiHopDong = loaiHopDong; }
    public BigDecimal getMucLuongDeNghi() { return mucLuongDeNghi; }
    public void setMucLuongDeNghi(BigDecimal mucLuongDeNghi) { this.mucLuongDeNghi = mucLuongDeNghi; }
    public LocalDate getNgayVaoLamDeNghi() { return ngayVaoLamDeNghi; }
    public void setNgayVaoLamDeNghi(LocalDate ngayVaoLamDeNghi) { this.ngayVaoLamDeNghi = ngayVaoLamDeNghi; }
    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public String getChucDanh() { return chucDanh; }
    public void setChucDanh(String chucDanh) { this.chucDanh = chucDanh; }
    public Integer getThoiHanThuViecThang() { return thoiHanThuViecThang; }
    public void setThoiHanThuViecThang(Integer thoiHanThuViecThang) { this.thoiHanThuViecThang = thoiHanThuViecThang; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public TrangThaiQuyetDinh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiQuyetDinh trangThai) { this.trangThai = trangThai; }
    public LocalDate getNgayUngVienPhanHoi() { return ngayUngVienPhanHoi; }
    public void setNgayUngVienPhanHoi(LocalDate ngayUngVienPhanHoi) { this.ngayUngVienPhanHoi = ngayUngVienPhanHoi; }
    public UUID getNhanVienMoiId() { return nhanVienMoiId; }
    public void setNhanVienMoiId(UUID nhanVienMoiId) { this.nhanVienMoiId = nhanVienMoiId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
