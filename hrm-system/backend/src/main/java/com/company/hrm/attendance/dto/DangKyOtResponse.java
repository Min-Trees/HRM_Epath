package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.DangKyOt;
import com.company.hrm.attendance.entity.HeSoOt;
import com.company.hrm.attendance.entity.TrangThaiDon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DangKyOtResponse {

    private UUID otId;
    private UUID nhanVienId;
    private LocalDate ngayLamOt;
    private OffsetDateTime gioBatDau;
    private OffsetDateTime gioKetThuc;
    private BigDecimal soGioOt;
    private HeSoOt heSoOt;
    private boolean lamDem;
    private String lyDo;
    private TrangThaiDon trangThai;
    private UUID duyetCap1Boi;
    private OffsetDateTime duyetCap1Luc;
    private UUID duyetCap2Boi;
    private OffsetDateTime duyetCap2Luc;
    private String ghiChuDuyet;

    public static DangKyOtResponse from(DangKyOt e) {
        DangKyOtResponse r = new DangKyOtResponse();
        r.otId = e.getOtId();
        r.nhanVienId = e.getNhanVienId();
        r.ngayLamOt = e.getNgayLamOt();
        r.gioBatDau = e.getGioBatDau();
        r.gioKetThuc = e.getGioKetThuc();
        r.soGioOt = e.getSoGioOt();
        r.heSoOt = e.getHeSoOt();
        r.lamDem = e.isLamDem();
        r.lyDo = e.getLyDo();
        r.trangThai = e.getTrangThai();
        r.duyetCap1Boi = e.getDuyetCap1Boi();
        r.duyetCap1Luc = e.getDuyetCap1Luc();
        r.duyetCap2Boi = e.getDuyetCap2Boi();
        r.duyetCap2Luc = e.getDuyetCap2Luc();
        r.ghiChuDuyet = e.getGhiChuDuyet();
        return r;
    }

    public UUID getOtId() { return otId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public LocalDate getNgayLamOt() { return ngayLamOt; }
    public OffsetDateTime getGioBatDau() { return gioBatDau; }
    public OffsetDateTime getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getSoGioOt() { return soGioOt; }
    public HeSoOt getHeSoOt() { return heSoOt; }
    public boolean isLamDem() { return lamDem; }
    public String getLyDo() { return lyDo; }
    public TrangThaiDon getTrangThai() { return trangThai; }
    public UUID getDuyetCap1Boi() { return duyetCap1Boi; }
    public OffsetDateTime getDuyetCap1Luc() { return duyetCap1Luc; }
    public UUID getDuyetCap2Boi() { return duyetCap2Boi; }
    public OffsetDateTime getDuyetCap2Luc() { return duyetCap2Luc; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
}