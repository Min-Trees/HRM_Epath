package com.company.hrm.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T20 - DangKyDaoTao.
 */
@Entity
@Table(name = "dang_ky_dao_tao", schema = "training")
public class DangKyDaoTao {

    @Id
    @GeneratedValue
    @Column(name = "dang_ky_dao_tao_id")
    private UUID id;

    private UUID lopHocId;

    private UUID nhanVienId;

    private LocalDateTime ngayDangKy;

    private TrangThaiDangKy trangThai;

    private String lyDoDangKy;

    private UUID nguoiDuyetId;

    private LocalDateTime ngayDuyet;

    private String ghiChuDuyet;

    private BigDecimal diemTongKet;

    private String chungChiCap;

    private LocalDate ngayCap;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }


    public UUID getLopHocId() { return lopHocId; }
    public void setLopHocId(UUID lopHocId) { this.lopHocId = lopHocId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LocalDateTime getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDateTime ngayDangKy) { this.ngayDangKy = ngayDangKy; }
    public TrangThaiDangKy getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDangKy trangThai) { this.trangThai = trangThai; }
    public String getLyDoDangKy() { return lyDoDangKy; }
    public void setLyDoDangKy(String lyDoDangKy) { this.lyDoDangKy = lyDoDangKy; }
    public UUID getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(UUID nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
    public void setGhiChuDuyet(String ghiChuDuyet) { this.ghiChuDuyet = ghiChuDuyet; }
    public BigDecimal getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(BigDecimal diemTongKet) { this.diemTongKet = diemTongKet; }
    public String getChungChiCap() { return chungChiCap; }
    public void setChungChiCap(String chungChiCap) { this.chungChiCap = chungChiCap; }
    public LocalDate getNgayCap() { return ngayCap; }
    public void setNgayCap(LocalDate ngayCap) { this.ngayCap = ngayCap; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
