package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.CaLamViec;
import com.company.hrm.attendance.entity.CaLamViec.LoaiCa;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public class CaLamViecResponse {

    private UUID caId;
    private String maCa;
    private String tenCa;
    private LoaiCa loaiCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private BigDecimal soGioChuan;
    private boolean quaNgay;
    private boolean active;
    private Instant createdAt;

    public static CaLamViecResponse from(CaLamViec e) {
        CaLamViecResponse r = new CaLamViecResponse();
        r.caId = e.getCaId();
        r.maCa = e.getMaCa();
        r.tenCa = e.getTenCa();
        r.loaiCa = e.getLoaiCa();
        r.gioBatDau = e.getGioBatDau();
        r.gioKetThuc = e.getGioKetThuc();
        r.soGioChuan = e.getSoGioChuan();
        r.quaNgay = e.isQuaNgay();
        r.active = e.isActive();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getCaId() { return caId; }
    public String getMaCa() { return maCa; }
    public String getTenCa() { return tenCa; }
    public LoaiCa getLoaiCa() { return loaiCa; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getSoGioChuan() { return soGioChuan; }
    public boolean isQuaNgay() { return quaNgay; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
