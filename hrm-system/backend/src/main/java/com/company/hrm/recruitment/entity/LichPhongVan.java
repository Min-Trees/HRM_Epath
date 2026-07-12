package com.company.hrm.recruitment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lich_phong_van", schema = "recruitment")
public class LichPhongVan {

    @Id
    @GeneratedValue
    @Column(name = "lich_pv_id")
    private UUID lichPvId;

    @Column(name = "ung_vien_id", nullable = false)
    private UUID ungVienId;

    @Column(name = "vong_phong_van", nullable = false)
    private Integer vongPhongVan;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "dia_diem", length = 300)
    private String diaDiem;

    @Column(name = "hinh_thuc", length = 50)
    private String hinhThuc = "TRUC_TIEP";

    @Column(name = "link_online", length = 500)
    private String linkOnline;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "nguoi_phong_van_ids", columnDefinition = "uuid[]")
    private UUID[] nguoiPhongVanIds = new UUID[0];

    @Column(name = "nguoi_to_chuc_id")
    private UUID nguoiToChucId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 30)
    private TrangThaiLichPV trangThai = TrangThaiLichPV.CHUA_DIEN_RA;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

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
    public UUID[] getNguoiPhongVanIds() { return nguoiPhongVanIds; }
    public void setNguoiPhongVanIds(UUID[] nguoiPhongVanIds) { this.nguoiPhongVanIds = nguoiPhongVanIds; }
    public UUID getNguoiToChucId() { return nguoiToChucId; }
    public void setNguoiToChucId(UUID nguoiToChucId) { this.nguoiToChucId = nguoiToChucId; }
    public TrangThaiLichPV getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLichPV trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
