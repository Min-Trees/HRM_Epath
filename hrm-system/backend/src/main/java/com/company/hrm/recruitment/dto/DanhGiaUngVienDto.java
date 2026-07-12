package com.company.hrm.recruitment.dto;

import com.company.hrm.recruitment.entity.KetQuaPhongVan;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DanhGiaUngVienDto {

    private UUID danhGiaId;

    @NotNull
    private UUID lichPvId;

    @NotNull
    private UUID nguoiDanhGiaId;

    @Min(0) @Max(10)
    private Integer diemKyThuat;

    @Min(0) @Max(10)
    private Integer diemGiaoTiep;

    @Min(0) @Max(10)
    private Integer diemThaiDo;

    @Min(0) @Max(10)
    private Integer diemKetQua;

    private BigDecimal diemTrungBinh;

    @NotNull
    private KetQuaPhongVan ketQua;

    private String diemManh;
    private String diemYeu;
    private String nhanXet;
    private LocalDateTime createdAt;

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
