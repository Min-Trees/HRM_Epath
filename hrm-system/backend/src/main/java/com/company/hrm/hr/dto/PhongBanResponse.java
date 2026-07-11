package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.PhongBan;

import java.time.Instant;
import java.util.UUID;

public class PhongBanResponse {
    private UUID phongBanId;
    private String maPhongBan;
    private String tenPhongBan;
    private UUID phongBanChaId;
    private UUID truongBoPhanId;
    private int dinhBien;
    private int capDo;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static PhongBanResponse from(PhongBan e) {
        PhongBanResponse r = new PhongBanResponse();
        r.phongBanId = e.getPhongBanId();
        r.maPhongBan = e.getMaPhongBan();
        r.tenPhongBan = e.getTenPhongBan();
        r.phongBanChaId = e.getPhongBanChaId();
        r.truongBoPhanId = e.getTruongBoPhanId();
        r.dinhBien = e.getDinhBien();
        r.capDo = e.getCapDo();
        r.active = e.isActive();
        r.createdAt = e.getCreatedAt();
        r.updatedAt = e.getUpdatedAt();
        return r;
    }

    public UUID getPhongBanId() { return phongBanId; }
    public String getMaPhongBan() { return maPhongBan; }
    public String getTenPhongBan() { return tenPhongBan; }
    public UUID getPhongBanChaId() { return phongBanChaId; }
    public UUID getTruongBoPhanId() { return truongBoPhanId; }
    public int getDinhBien() { return dinhBien; }
    public int getCapDo() { return capDo; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}