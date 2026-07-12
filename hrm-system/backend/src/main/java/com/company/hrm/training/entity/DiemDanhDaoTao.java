package com.company.hrm.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T20 - DiemDanhDaoTao.
 */
@Entity
@Table(name = "diem_danh_dao_tao", schema = "training")
public class DiemDanhDaoTao {

    @Id
    @GeneratedValue
    @Column(name = "diem_danh_dao_tao_id")
    private UUID id;

    private UUID dangKyId;

    private Integer buoiSo;

    private LocalDate ngayHoc;

    private TrangThaiThamDu trangThai;

    private BigDecimal diemBaiTap;

    private String ghiChu;

    private UUID nguoiDiemDanhId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }


    public UUID getDangKyId() { return dangKyId; }
    public void setDangKyId(UUID dangKyId) { this.dangKyId = dangKyId; }
    public Integer getBuoiSo() { return buoiSo; }
    public void setBuoiSo(Integer buoiSo) { this.buoiSo = buoiSo; }
    public LocalDate getNgayHoc() { return ngayHoc; }
    public void setNgayHoc(LocalDate ngayHoc) { this.ngayHoc = ngayHoc; }
    public TrangThaiThamDu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiThamDu trangThai) { this.trangThai = trangThai; }
    public BigDecimal getDiemBaiTap() { return diemBaiTap; }
    public void setDiemBaiTap(BigDecimal diemBaiTap) { this.diemBaiTap = diemBaiTap; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiDiemDanhId() { return nguoiDiemDanhId; }
    public void setNguoiDiemDanhId(UUID nguoiDiemDanhId) { this.nguoiDiemDanhId = nguoiDiemDanhId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
