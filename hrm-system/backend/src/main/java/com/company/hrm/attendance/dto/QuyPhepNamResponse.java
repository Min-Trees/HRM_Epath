package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.QuyPhepNam;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response quỹ phép năm của NV. {@code soNgayConLai} mirror cột GENERATED ở DB.
 */
public class QuyPhepNamResponse {

    private UUID quyPhepId;
    private UUID nhanVienId;
    private Integer nam;
    private BigDecimal soNgayDuocHuong;
    private BigDecimal soNgayDaDung;
    private BigDecimal soNgayConLai;

    public QuyPhepNamResponse() {}

    public QuyPhepNamResponse(UUID quyPhepId, UUID nhanVienId, Integer nam,
                              BigDecimal soNgayDuocHuong, BigDecimal soNgayDaDung,
                              BigDecimal soNgayConLai) {
        this.quyPhepId = quyPhepId;
        this.nhanVienId = nhanVienId;
        this.nam = nam;
        this.soNgayDuocHuong = soNgayDuocHuong;
        this.soNgayDaDung = soNgayDaDung;
        this.soNgayConLai = soNgayConLai;
    }

    public static QuyPhepNamResponse from(QuyPhepNam e) {
        QuyPhepNamResponse r = new QuyPhepNamResponse();
        r.quyPhepId = e.getQuyPhepId();
        r.nhanVienId = e.getNhanVienId();
        r.nam = e.getNam();
        r.soNgayDuocHuong = e.getSoNgayDuocHuong();
        r.soNgayDaDung = e.getSoNgayDaDung();
        r.soNgayConLai = e.getSoNgayConLai();
        return r;
    }

    public UUID getQuyPhepId() { return quyPhepId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public Integer getNam() { return nam; }
    public BigDecimal getSoNgayDuocHuong() { return soNgayDuocHuong; }
    public BigDecimal getSoNgayDaDung() { return soNgayDaDung; }
    public BigDecimal getSoNgayConLai() { return soNgayConLai; }
}