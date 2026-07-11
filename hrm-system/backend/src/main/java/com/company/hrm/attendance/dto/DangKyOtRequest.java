package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.HeSoOt;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Request tạo đăng ký OT. {@code heSoOt} do client (HR/Manager) truyền theo ngày.
 */
public class DangKyOtRequest {

    @NotNull
    private UUID nhanVienId;

    @NotNull
    private LocalDate ngayLamOt;

    @NotNull
    private OffsetDateTime gioBatDau;

    @NotNull
    private OffsetDateTime gioKetThuc;

    @NotNull
    private HeSoOt heSoOt;

    private Boolean lamDem = Boolean.FALSE;

    @Size(max = 500)
    private String lyDo;

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LocalDate getNgayLamOt() { return ngayLamOt; }
    public void setNgayLamOt(LocalDate ngayLamOt) { this.ngayLamOt = ngayLamOt; }
    public OffsetDateTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(OffsetDateTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public OffsetDateTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(OffsetDateTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public HeSoOt getHeSoOt() { return heSoOt; }
    public void setHeSoOt(HeSoOt heSoOt) { this.heSoOt = heSoOt; }
    public Boolean getLamDem() { return lamDem; }
    public void setLamDem(Boolean lamDem) { this.lamDem = lamDem; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}