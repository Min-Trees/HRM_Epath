package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.NgachBacLuong;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class NgachBacLuongResponse {
    private UUID ngachBacId;
    private String maNgach;
    private String tenChucDanh;
    private int bacLuong;
    private BigDecimal heSoLuong;
    private BigDecimal luongCoBanToiThieu;
    private boolean active;
    private Instant createdAt;

    public static NgachBacLuongResponse from(NgachBacLuong e) {
        NgachBacLuongResponse r = new NgachBacLuongResponse();
        r.ngachBacId = e.getNgachBacId();
        r.maNgach = e.getMaNgach();
        r.tenChucDanh = e.getTenChucDanh();
        r.bacLuong = e.getBacLuong();
        r.heSoLuong = e.getHeSoLuong();
        r.luongCoBanToiThieu = e.getLuongCoBanToiThieu();
        r.active = e.isActive();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getNgachBacId() { return ngachBacId; }
    public String getMaNgach() { return maNgach; }
    public String getTenChucDanh() { return tenChucDanh; }
    public int getBacLuong() { return bacLuong; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public BigDecimal getLuongCoBanToiThieu() { return luongCoBanToiThieu; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}