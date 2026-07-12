package com.company.hrm.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T20 - ChuongTrinhDaoTao.
 */
@Entity
@Table(name = "chuong_trinh_dao_tao", schema = "training")
public class ChuongTrinhDaoTao {

    @Id
    @GeneratedValue
    @Column(name = "chuong_trinh_dao_tao_id")
    private UUID id;

    private String maChuongTrinh;

    private String tenChuongTrinh;

    private LoaiChuongTrinh loaiChuongTrinh;

    private String moTa;

    private String mucTieu;

    private BigDecimal thoiLuongGio;

    private BigDecimal diemDanhGiaToiThieu;

    private String chungChi;

    private TrangThaiChuongTrinh trangThai;

    private UUID nguoiTaoId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }


    public String getMaChuongTrinh() { return maChuongTrinh; }
    public void setMaChuongTrinh(String maChuongTrinh) { this.maChuongTrinh = maChuongTrinh; }
    public String getTenChuongTrinh() { return tenChuongTrinh; }
    public void setTenChuongTrinh(String tenChuongTrinh) { this.tenChuongTrinh = tenChuongTrinh; }
    public LoaiChuongTrinh getLoaiChuongTrinh() { return loaiChuongTrinh; }
    public void setLoaiChuongTrinh(LoaiChuongTrinh loaiChuongTrinh) { this.loaiChuongTrinh = loaiChuongTrinh; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getMucTieu() { return mucTieu; }
    public void setMucTieu(String mucTieu) { this.mucTieu = mucTieu; }
    public BigDecimal getThoiLuongGio() { return thoiLuongGio; }
    public void setThoiLuongGio(BigDecimal thoiLuongGio) { this.thoiLuongGio = thoiLuongGio; }
    public BigDecimal getDiemDanhGiaToiThieu() { return diemDanhGiaToiThieu; }
    public void setDiemDanhGiaToiThieu(BigDecimal diemDanhGiaToiThieu) { this.diemDanhGiaToiThieu = diemDanhGiaToiThieu; }
    public String getChungChi() { return chungChi; }
    public void setChungChi(String chungChi) { this.chungChi = chungChi; }
    public TrangThaiChuongTrinh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiChuongTrinh trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
