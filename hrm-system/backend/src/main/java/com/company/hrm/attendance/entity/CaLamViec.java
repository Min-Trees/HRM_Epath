package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "ca_lam_viec", schema = "timekeeping",
        uniqueConstraints = @UniqueConstraint(name = "ca_lam_viec_ma_ca_key", columnNames = "ma_ca"))
public class CaLamViec extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "ca_id")
    private UUID caId;

    @Column(name = "ma_ca", nullable = false, length = 20)
    private String maCa;

    @Column(name = "ten_ca", nullable = false, length = 100)
    private String tenCa;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_ca", nullable = false)
    private LoaiCa loaiCa;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "so_gio_chuan", nullable = false, precision = 4, scale = 2)
    private BigDecimal soGioChuan = new BigDecimal("8.00");

    @Column(name = "qua_ngay", nullable = false)
    private boolean quaNgay = false;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public enum LoaiCa {
        HANH_CHINH, CA_KIP, FLEXIBLE
    }

    public UUID getCaId() { return caId; }
    public void setCaId(UUID caId) { this.caId = caId; }
    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public LoaiCa getLoaiCa() { return loaiCa; }
    public void setLoaiCa(LoaiCa loaiCa) { this.loaiCa = loaiCa; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public BigDecimal getSoGioChuan() { return soGioChuan; }
    public void setSoGioChuan(BigDecimal soGioChuan) { this.soGioChuan = soGioChuan; }
    public boolean isQuaNgay() { return quaNgay; }
    public void setQuaNgay(boolean quaNgay) { this.quaNgay = quaNgay; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
