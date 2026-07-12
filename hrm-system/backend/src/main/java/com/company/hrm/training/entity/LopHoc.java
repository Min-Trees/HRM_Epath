package com.company.hrm.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T20 - LopHoc.
 */
@Entity
@Table(name = "lop_hoc", schema = "training")
public class LopHoc {

    @Id
    @GeneratedValue
    @Column(name = "lop_hoc_id")
    private UUID id;

    private String maLop;

    private UUID chuongTrinhId;

    private String tenLop;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private Integer soBuoi;

    private Integer soChoToiDa;

    private String diaDiem;

    private String giangVien;

    private BigDecimal chiPhiMoiNv;

    private TrangThaiLop trangThai;

    private String ghiChu;

    private UUID nguoiPhuTrachId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }


    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }
    public UUID getChuongTrinhId() { return chuongTrinhId; }
    public void setChuongTrinhId(UUID chuongTrinhId) { this.chuongTrinhId = chuongTrinhId; }
    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public Integer getSoBuoi() { return soBuoi; }
    public void setSoBuoi(Integer soBuoi) { this.soBuoi = soBuoi; }
    public Integer getSoChoToiDa() { return soChoToiDa; }
    public void setSoChoToiDa(Integer soChoToiDa) { this.soChoToiDa = soChoToiDa; }
    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }
    public String getGiangVien() { return giangVien; }
    public void setGiangVien(String giangVien) { this.giangVien = giangVien; }
    public BigDecimal getChiPhiMoiNv() { return chiPhiMoiNv; }
    public void setChiPhiMoiNv(BigDecimal chiPhiMoiNv) { this.chiPhiMoiNv = chiPhiMoiNv; }
    public TrangThaiLop getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLop trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getNguoiPhuTrachId() { return nguoiPhuTrachId; }
    public void setNguoiPhuTrachId(UUID nguoiPhuTrachId) { this.nguoiPhuTrachId = nguoiPhuTrachId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
