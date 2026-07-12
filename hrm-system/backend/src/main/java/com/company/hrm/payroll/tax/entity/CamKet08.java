package com.company.hrm.payroll.tax.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T16 - Cam ket 08 cho quyet toan thue TNCN cuoi nam.
 *
 * <p>Moi NV chi co 1 ban ghi cam ket 08 cho moi nam (UNIQUE constraint).
 * Neu NV uy quyen QTT (loai = UY_QUYEN_QTT), DN phai liet ke NV vao
 * mau 02/QTT va 05/QTT; neu khong, chi liet ke nhung NV co thu nhap
 * chiu thue.
 */
@Entity
@Table(name = "cam_ket_08", schema = "payroll")
public class CamKet08 {

    @Id
    @GeneratedValue
    @Column(name = "cam_ket_id")
    private UUID camKetId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "nam", nullable = false)
    private Integer nam;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    @Column(name = "loai_cam_ket", nullable = false, columnDefinition = "payroll.loai_cam_ket_08")
    private LoaiCamKet08 loaiCamKet;

    @Column(name = "ngay_dang_ky", nullable = false)
    private LocalDate ngayDangKy;

    @Column(name = "hieu_luc_tu_ngay", nullable = false)
    private LocalDate hieuLucTuNgay;

    @Column(name = "hieu_luc_den_ngay")
    private LocalDate hieuLucDenNgay;

    @Column(name = "uy_quyen_qtt", nullable = false)
    private Boolean uyQuyenQtt = false;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getCamKetId() { return camKetId; }
    public void setCamKetId(UUID camKetId) { this.camKetId = camKetId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public LoaiCamKet08 getLoaiCamKet() { return loaiCamKet; }
    public void setLoaiCamKet(LoaiCamKet08 loaiCamKet) { this.loaiCamKet = loaiCamKet; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public LocalDate getHieuLucTuNgay() { return hieuLucTuNgay; }
    public void setHieuLucTuNgay(LocalDate hieuLucTuNgay) { this.hieuLucTuNgay = hieuLucTuNgay; }
    public LocalDate getHieuLucDenNgay() { return hieuLucDenNgay; }
    public void setHieuLucDenNgay(LocalDate hieuLucDenNgay) { this.hieuLucDenNgay = hieuLucDenNgay; }
    public Boolean getUyQuyenQtt() { return uyQuyenQtt; }
    public void setUyQuyenQtt(Boolean uyQuyenQtt) { this.uyQuyenQtt = uyQuyenQtt; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
