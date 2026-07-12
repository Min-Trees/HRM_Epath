package com.company.hrm.recruitment.dto;

import com.company.hrm.recruitment.entity.TrangThaiYeuCau;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class YeuCauTuyenDungDto {

    private UUID yeuCauId;
    private String maYeuCau;

    @NotBlank
    @Size(max = 200)
    private String tieuDe;

    @NotNull
    private UUID phongBanId;

    @NotNull
    private UUID nguoiYeuCauId;

    @NotNull
    @Min(1)
    private Integer soLuongCan;

    private String lyDo;
    private BigDecimal mucLuongDeXuat;
    private TrangThaiYeuCau trangThai;
    private UUID nguoiPheDuyetId;
    private LocalDateTime ngayPheDuyet;
    private LocalDate ngayCanTuyen;
    private LocalDate ngayDongYeuCau;

    private String tenPhongBan;
    private String tenNguoiYeuCau;
    private long soUngVien;

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
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
    public String getTenNguoiYeuCau() { return tenNguoiYeuCau; }
    public void setTenNguoiYeuCau(String tenNguoiYeuCau) { this.tenNguoiYeuCau = tenNguoiYeuCau; }
    public long getSoUngVien() { return soUngVien; }
    public void setSoUngVien(long soUngVien) { this.soUngVien = soUngVien; }
}
