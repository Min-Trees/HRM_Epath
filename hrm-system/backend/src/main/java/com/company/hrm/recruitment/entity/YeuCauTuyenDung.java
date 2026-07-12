package com.company.hrm.recruitment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T17 - Yeu cau tuyen dung.
 *
 * <p>Manager / HR tao yeu cau, HR phe duyet, ung vien se duoc them vao
 * tu yeu cau (yeu_cau_id FK trong ung_vien).
 */
@Entity
@Table(name = "yeu_cau_tuyen_dung", schema = "recruitment")
public class YeuCauTuyenDung {

    @Id
    @GeneratedValue
    @Column(name = "yeu_cau_id")
    private UUID yeuCauId;

    @Column(name = "ma_yeu_cau", length = 20, unique = true)
    private String maYeuCau;

    @Column(name = "tieu_de", length = 200, nullable = false)
    private String tieuDe;

    @Column(name = "phong_ban_id", nullable = false)
    private UUID phongBanId;

    @Column(name = "nguoi_yeu_cau_id", nullable = false)
    private UUID nguoiYeuCauId;

    @Column(name = "so_luong_can", nullable = false)
    private Integer soLuongCan;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "muc_luong_de_xuat", precision = 14, scale = 2)
    private BigDecimal mucLuongDeXuat;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "recruitment.trang_thai_yeu_cau")
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.MOI_TAO;

    @Column(name = "nguoi_phe_duyet_id")
    private UUID nguoiPheDuyetId;

    @Column(name = "ngay_phe_duyet")
    private LocalDateTime ngayPheDuyet;

    @Column(name = "ngay_can_tuyen")
    private LocalDate ngayCanTuyen;

    @Column(name = "ngay_dong_yeu_cau")
    private LocalDate ngayDongYeuCau;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getYeuCauId() { return yeuCauId; }
    public void setYeuCauId(UUID yeuCauId) { this.yeuCauId = yeuCauId; }
    public String getMaYeuCau() { return maYeuCau; }
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public UUID getNguoiYeuCauId() { return nguoiYeuCauId; }
    public void setNguoiYeuCauId(UUID nguoiYeuCauId) { this.nguoiYeuCauId = nguoiYeuCauId; }
    public Integer getSoLuongCan() { return soLuongCan; }
    public void setSoLuongCan(Integer soLuongCan) { this.soLuongCan = soLuongCan; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public BigDecimal getMucLuongDeXuat() { return mucLuongDeXuat; }
    public void setMucLuongDeXuat(BigDecimal mucLuongDeXuat) { this.mucLuongDeXuat = mucLuongDeXuat; }
    public TrangThaiYeuCau getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiYeuCau trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiPheDuyetId() { return nguoiPheDuyetId; }
    public void setNguoiPheDuyetId(UUID nguoiPheDuyetId) { this.nguoiPheDuyetId = nguoiPheDuyetId; }
    public LocalDateTime getNgayPheDuyet() { return ngayPheDuyet; }
    public void setNgayPheDuyet(LocalDateTime ngayPheDuyet) { this.ngayPheDuyet = ngayPheDuyet; }
    public LocalDate getNgayCanTuyen() { return ngayCanTuyen; }
    public void setNgayCanTuyen(LocalDate ngayCanTuyen) { this.ngayCanTuyen = ngayCanTuyen; }
    public LocalDate getNgayDongYeuCau() { return ngayDongYeuCau; }
    public void setNgayDongYeuCau(LocalDate ngayDongYeuCau) { this.ngayDongYeuCau = ngayDongYeuCau; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
