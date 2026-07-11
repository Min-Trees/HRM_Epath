package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.LoaiNghiPhep;
import com.company.hrm.attendance.entity.NghiPhep;
import com.company.hrm.attendance.entity.TrangThaiDon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class NghiPhepResponse {

    private UUID nghiPhepId;
    private UUID nhanVienId;
    private LoaiNghiPhep loaiNghiPhep;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private BigDecimal soNgayNghi;
    private String lyDo;
    private String fileDinhKemUrl;
    private TrangThaiDon trangThai;
    private UUID duyetCap1Boi;
    private OffsetDateTime duyetCap1Luc;
    private UUID duyetCap2Boi;
    private OffsetDateTime duyetCap2Luc;
    private String ghiChuDuyet;

    public static NghiPhepResponse from(NghiPhep e) {
        NghiPhepResponse r = new NghiPhepResponse();
        r.nghiPhepId = e.getNghiPhepId();
        r.nhanVienId = e.getNhanVienId();
        r.loaiNghiPhep = e.getLoaiNghiPhep();
        r.tuNgay = e.getTuNgay();
        r.denNgay = e.getDenNgay();
        r.soNgayNghi = e.getSoNgayNghi();
        r.lyDo = e.getLyDo();
        r.fileDinhKemUrl = e.getFileDinhKemUrl();
        r.trangThai = e.getTrangThai();
        r.duyetCap1Boi = e.getDuyetCap1Boi();
        r.duyetCap1Luc = e.getDuyetCap1Luc();
        r.duyetCap2Boi = e.getDuyetCap2Boi();
        r.duyetCap2Luc = e.getDuyetCap2Luc();
        r.ghiChuDuyet = e.getGhiChuDuyet();
        return r;
    }

    public UUID getNghiPhepId() { return nghiPhepId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public LoaiNghiPhep getLoaiNghiPhep() { return loaiNghiPhep; }
    public LocalDate getTuNgay() { return tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public BigDecimal getSoNgayNghi() { return soNgayNghi; }
    public String getLyDo() { return lyDo; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public TrangThaiDon getTrangThai() { return trangThai; }
    public UUID getDuyetCap1Boi() { return duyetCap1Boi; }
    public OffsetDateTime getDuyetCap1Luc() { return duyetCap1Luc; }
    public UUID getDuyetCap2Boi() { return duyetCap2Boi; }
    public OffsetDateTime getDuyetCap2Luc() { return duyetCap2Luc; }
    public String getGhiChuDuyet() { return ghiChuDuyet; }
}