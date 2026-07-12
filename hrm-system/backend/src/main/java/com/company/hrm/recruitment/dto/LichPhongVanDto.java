package com.company.hrm.recruitment.dto;

import com.company.hrm.recruitment.entity.TrangThaiLichPV;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LichPhongVanDto {

    private UUID lichPvId;

    @NotNull
    private UUID ungVienId;

    @NotNull
    private Integer vongPhongVan;

    @NotNull
    private LocalDateTime thoiGianBatDau;

    @NotNull
    private LocalDateTime thoiGianKetThuc;

    private String diaDiem;
    private String hinhThuc;
    private String linkOnline;
    private List<UUID> nguoiPhongVanIds;
    private UUID nguoiToChucId;
    private TrangThaiLichPV trangThai;
    private String ghiChu;
    private String tenUngVien;
    private String maUngVien;

    public UUID getLichPvId() { return lichPvId; }
    public void setLichPvId(UUID lichPvId) { this.lichPvId = lichPvId; }
    public UUID getUngVienId() { return ungVienId; }
    public void setUngVienId(UUID ungVienId) { this.ungVienId = ungVienId; }
    public Integer getVongPhongVan() { return vongPhongVan; }
    public void setVongPhongVan(Integer vongPhongVan) { this.vongPhongVan = vongPhongVan; }
    public LocalDateTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public LocalDateTime getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }
    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }
    public String getLinkOnline() { return linkOnline; }
    public void setLinkOnline(String linkOnline) { this.linkOnline = linkOnline; }
    public List<UUID> getNguoiPhongVanIds() { return nguoiPhongVanIds; }
    public void setNguoiPhongVanIds(List<UUID> nguoiPhongVanIds) { this.nguoiPhongVanIds = nguoiPhongVanIds; }
    public UUID getNguoiToChucId() { return nguoiToChucId; }
    public void setNguoiToChucId(UUID nguoiToChucId) { this.nguoiToChucId = nguoiToChucId; }
    public TrangThaiLichPV getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLichPV trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getTenUngVien() { return tenUngVien; }
    public void setTenUngVien(String tenUngVien) { this.tenUngVien = tenUngVien; }
    public String getMaUngVien() { return maUngVien; }
    public void setMaUngVien(String maUngVien) { this.maUngVien = maUngVien; }
}
