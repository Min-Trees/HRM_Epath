package com.company.hrm.hr.dto;

import com.company.hrm.hr.entity.QuaTrinhCongTac;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class QuaTrinhCongTacResponse {
    private UUID quaTrinhId;
    private UUID nhanVienId;
    private String donVi;
    private String chucDanh;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private String moTa;
    private Instant createdAt;

    public static QuaTrinhCongTacResponse from(QuaTrinhCongTac e) {
        QuaTrinhCongTacResponse r = new QuaTrinhCongTacResponse();
        r.quaTrinhId = e.getQuaTrinhId();
        r.nhanVienId = e.getNhanVienId();
        r.donVi = e.getDonVi();
        r.chucDanh = e.getChucDanh();
        r.tuNgay = e.getTuNgay();
        r.denNgay = e.getDenNgay();
        r.moTa = e.getMoTa();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public UUID getQuaTrinhId() { return quaTrinhId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public String getDonVi() { return donVi; }
    public String getChucDanh() { return chucDanh; }
    public LocalDate getTuNgay() { return tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public String getMoTa() { return moTa; }
    public Instant getCreatedAt() { return createdAt; }
}