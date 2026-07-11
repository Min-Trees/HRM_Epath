package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.CreatedOnlyAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "phan_ca", schema = "timekeeping",
        uniqueConstraints = @UniqueConstraint(name = "uq_phan_ca", columnNames = {"nhan_vien_id", "ngay_ap_dung"}))
public class PhanCa extends CreatedOnlyAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "phan_ca_id")
    private UUID phanCaId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "ca_id", nullable = false)
    private UUID caId;

    @Column(name = "ngay_ap_dung", nullable = false)
    private LocalDate ngayApDung;

    @Column(name = "ghi_chu", length = 300)
    private String ghiChu;

    public UUID getPhanCaId() { return phanCaId; }
    public void setPhanCaId(UUID phanCaId) { this.phanCaId = phanCaId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public UUID getCaId() { return caId; }
    public void setCaId(UUID caId) { this.caId = caId; }
    public LocalDate getNgayApDung() { return ngayApDung; }
    public void setNgayApDung(LocalDate ngayApDung) { this.ngayApDung = ngayApDung; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
