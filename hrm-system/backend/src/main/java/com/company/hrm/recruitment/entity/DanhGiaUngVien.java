package com.company.hrm.recruitment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T17 - Bang diem danh gia cua nguoi phong van cho ung vien.
 *
 * <p>Cac cot diem: ky_thuat, giao_tiep, thai_do, ket_qua. Cot
 * diem_trung_binh la cot generated (tinh o DB).
 */
@Entity
@Table(name = "danh_gia_ung_vien", schema = "recruitment")
public class DanhGiaUngVien {

    @Id
    @GeneratedValue
    @Column(name = "danh_gia_id")
    private UUID danhGiaId;

    @Column(name = "lich_pv_id", nullable = false)
    private UUID lichPvId;

    @Column(name = "nguoi_danh_gia_id", nullable = false)
    private UUID nguoiDanhGiaId;

    @Column(name = "diem_ky_thuat")
    private Integer diemKyThuat;

    @Column(name = "diem_giao_tiep")
    private Integer diemGiaoTiep;

    @Column(name = "diem_thai_do")
    private Integer diemThaiDo;

    @Column(name = "diem_ket_qua")
    private Integer diemKetQua;

    @Column(name = "diem_trung_binh", insertable = false, updatable = false)
    private BigDecimal diemTrungBinh;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ket_qua", nullable = false, columnDefinition = "recruitment.ket_qua_phong_van")
    private KetQuaPhongVan ketQua;

    @Column(name = "diem_manh", columnDefinition = "TEXT")
    private String diemManh;

    @Column(name = "diem_yeu", columnDefinition = "TEXT")
    private String diemYeu;

    @Column(name = "nhan_xet", columnDefinition = "TEXT")
    private String nhanXet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public UUID getDanhGiaId() { return danhGiaId; }
    public void setDanhGiaId(UUID danhGiaId) { this.danhGiaId = danhGiaId; }
    public UUID getLichPvId() { return lichPvId; }
    public void setLichPvId(UUID lichPvId) { this.lichPvId = lichPvId; }
    public UUID getNguoiDanhGiaId() { return nguoiDanhGiaId; }
    public void setNguoiDanhGiaId(UUID nguoiDanhGiaId) { this.nguoiDanhGiaId = nguoiDanhGiaId; }
    public Integer getDiemKyThuat() { return diemKyThuat; }
    public void setDiemKyThuat(Integer diemKyThuat) { this.diemKyThuat = diemKyThuat; }
    public Integer getDiemGiaoTiep() { return diemGiaoTiep; }
    public void setDiemGiaoTiep(Integer diemGiaoTiep) { this.diemGiaoTiep = diemGiaoTiep; }
    public Integer getDiemThaiDo() { return diemThaiDo; }
    public void setDiemThaiDo(Integer diemThaiDo) { this.diemThaiDo = diemThaiDo; }
    public Integer getDiemKetQua() { return diemKetQua; }
    public void setDiemKetQua(Integer diemKetQua) { this.diemKetQua = diemKetQua; }
    public BigDecimal getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(BigDecimal diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }
    public KetQuaPhongVan getKetQua() { return ketQua; }
    public void setKetQua(KetQuaPhongVan ketQua) { this.ketQua = ketQua; }
    public String getDiemManh() { return diemManh; }
    public void setDiemManh(String diemManh) { this.diemManh = diemManh; }
    public String getDiemYeu() { return diemYeu; }
    public void setDiemYeu(String diemYeu) { this.diemYeu = diemYeu; }
    public String getNhanXet() { return nhanXet; }
    public void setNhanXet(String nhanXet) { this.nhanXet = nhanXet; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
