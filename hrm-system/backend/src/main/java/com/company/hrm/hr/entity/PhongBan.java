package com.company.hrm.hr.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "phong_ban", schema = "hr",
        uniqueConstraints = @UniqueConstraint(name = "phong_ban_ma_phong_ban_key", columnNames = "ma_phong_ban"))
public class PhongBan extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "phong_ban_id")
    private UUID phongBanId;

    @Column(name = "ma_phong_ban", nullable = false, length = 20)
    private String maPhongBan;

    @Column(name = "ten_phong_ban", nullable = false, length = 200)
    private String tenPhongBan;

    @Column(name = "phong_ban_cha_id")
    private UUID phongBanChaId;

    @Column(name = "truong_bo_phan_id")
    private UUID truongBoPhanId;

    @Column(name = "dinh_bien", nullable = false)
    private int dinhBien;

    @Column(name = "cap_do", nullable = false)
    private int capDo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public UUID getPhongBanId() { return phongBanId; }
    public void setPhongBanId(UUID phongBanId) { this.phongBanId = phongBanId; }
    public String getMaPhongBan() { return maPhongBan; }
    public void setMaPhongBan(String maPhongBan) { this.maPhongBan = maPhongBan; }
    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }
    public UUID getPhongBanChaId() { return phongBanChaId; }
    public void setPhongBanChaId(UUID phongBanChaId) { this.phongBanChaId = phongBanChaId; }
    public UUID getTruongBoPhanId() { return truongBoPhanId; }
    public void setTruongBoPhanId(UUID truongBoPhanId) { this.truongBoPhanId = truongBoPhanId; }
    public int getDinhBien() { return dinhBien; }
    public void setDinhBien(int dinhBien) { this.dinhBien = dinhBien; }
    public int getCapDo() { return capDo; }
    public void setCapDo(int capDo) { this.capDo = capDo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}