package com.company.hrm.hr.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ngach_bac_luong", schema = "hr",
        uniqueConstraints = @UniqueConstraint(name = "ngach_bac_luong_ma_ngach_key", columnNames = "ma_ngach"))
public class NgachBacLuong {

    @Id
    @GeneratedValue
    @Column(name = "ngach_bac_id")
    private UUID ngachBacId;

    @Column(name = "ma_ngach", nullable = false, length = 20)
    private String maNgach;

    @Column(name = "ten_chuc_danh", nullable = false, length = 200)
    private String tenChucDanh;

    @Column(name = "bac_luong", nullable = false)
    private int bacLuong;

    @Column(name = "he_so_luong", nullable = false, precision = 6, scale = 2)
    private BigDecimal heSoLuong;

    @Column(name = "luong_co_ban_toi_thieu", precision = 14, scale = 2)
    private BigDecimal luongCoBanToiThieu;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getNgachBacId() { return ngachBacId; }
    public void setNgachBacId(UUID ngachBacId) { this.ngachBacId = ngachBacId; }
    public String getMaNgach() { return maNgach; }
    public void setMaNgach(String maNgach) { this.maNgach = maNgach; }
    public String getTenChucDanh() { return tenChucDanh; }
    public void setTenChucDanh(String tenChucDanh) { this.tenChucDanh = tenChucDanh; }
    public int getBacLuong() { return bacLuong; }
    public void setBacLuong(int bacLuong) { this.bacLuong = bacLuong; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { this.heSoLuong = heSoLuong; }
    public BigDecimal getLuongCoBanToiThieu() { return luongCoBanToiThieu; }
    public void setLuongCoBanToiThieu(BigDecimal luongCoBanToiThieu) { this.luongCoBanToiThieu = luongCoBanToiThieu; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}