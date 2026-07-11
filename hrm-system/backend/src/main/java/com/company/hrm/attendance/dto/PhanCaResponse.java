package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.PhanCa;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PhanCaResponse {

    private UUID phanCaId;
    private UUID nhanVienId;
    private UUID caId;
    private String maCa;
    private String tenCa;
    private LocalDate ngayApDung;
    private String ghiChu;
    private Instant createdAt;

    public static PhanCaResponse from(PhanCa e) {
        PhanCaResponse r = new PhanCaResponse();
        r.phanCaId = e.getPhanCaId();
        r.nhanVienId = e.getNhanVienId();
        r.caId = e.getCaId();
        r.ngayApDung = e.getNgayApDung();
        r.ghiChu = e.getGhiChu();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public static PhanCaResponse from(PhanCa e, String maCa, String tenCa) {
        PhanCaResponse r = from(e);
        r.maCa = maCa;
        r.tenCa = tenCa;
        return r;
    }

    public UUID getPhanCaId() { return phanCaId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public UUID getCaId() { return caId; }
    public String getMaCa() { return maCa; }
    public String getTenCa() { return tenCa; }
    public LocalDate getNgayApDung() { return ngayApDung; }
    public String getGhiChu() { return ghiChu; }
    public Instant getCreatedAt() { return createdAt; }
}
