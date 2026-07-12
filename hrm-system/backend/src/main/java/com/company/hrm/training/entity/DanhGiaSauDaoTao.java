package com.company.hrm.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T20 - DanhGiaSauDaoTao.
 */
@Entity
@Table(name = "danh_gia_sau_dao_tao", schema = "training")
public class DanhGiaSauDaoTao {

    @Id
    @GeneratedValue
    @Column(name = "danh_gia_sau_dao_tao_id")
    private UUID id;

    private UUID dangKyId;

    private BigDecimal diemNoiDung;

    private BigDecimal diemGiangVien;

    private BigDecimal diemThucHanh;

    private KetQuaDanhGia ketQua;

    private String yKienNguoiHoc;

    private String yKienGv;

    private LocalDateTime ngayDanhGia;

    private UUID nguoiDanhGiaId;


    @Column(name = "diem_trung_binh", insertable = false, updatable = false)
    private BigDecimal diemTrungBinh;

    public BigDecimal getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(BigDecimal diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }


    public UUID getDangKyId() { return dangKyId; }
    public void setDangKyId(UUID dangKyId) { this.dangKyId = dangKyId; }
    public BigDecimal getDiemNoiDung() { return diemNoiDung; }
    public void setDiemNoiDung(BigDecimal diemNoiDung) { this.diemNoiDung = diemNoiDung; }
    public BigDecimal getDiemGiangVien() { return diemGiangVien; }
    public void setDiemGiangVien(BigDecimal diemGiangVien) { this.diemGiangVien = diemGiangVien; }
    public BigDecimal getDiemThucHanh() { return diemThucHanh; }
    public void setDiemThucHanh(BigDecimal diemThucHanh) { this.diemThucHanh = diemThucHanh; }
    public KetQuaDanhGia getKetQua() { return ketQua; }
    public void setKetQua(KetQuaDanhGia ketQua) { this.ketQua = ketQua; }
    public String getYKienNguoiHoc() { return yKienNguoiHoc; }
    public void setYKienNguoiHoc(String yKienNguoiHoc) { this.yKienNguoiHoc = yKienNguoiHoc; }
    public String getYKienGv() { return yKienGv; }
    public void setYKienGv(String yKienGv) { this.yKienGv = yKienGv; }
    public LocalDateTime getNgayDanhGia() { return ngayDanhGia; }
    public void setNgayDanhGia(LocalDateTime ngayDanhGia) { this.ngayDanhGia = ngayDanhGia; }
    public UUID getNguoiDanhGiaId() { return nguoiDanhGiaId; }
    public void setNguoiDanhGiaId(UUID nguoiDanhGiaId) { this.nguoiDanhGiaId = nguoiDanhGiaId; }
}
