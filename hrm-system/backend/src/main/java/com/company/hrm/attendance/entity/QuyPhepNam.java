package com.company.hrm.attendance.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity cho bảng {@code timekeeping.quy_phep_nam}.
 *
 * <p>{@code soNgayConLai} là cột GENERATED ở Postgres (computed từ {@code so_ngay_duoc_huong - so_ngay_da_dung}),
 * nên tầng Java chỉ đọc ({@link GenerationTime#ALWAYS}), không INSERT/UPDATE.
 */
@Entity
@Table(name = "quy_phep_nam", schema = "timekeeping")
public class QuyPhepNam extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "quy_phep_id")
    private UUID quyPhepId;

    @Column(name = "nhan_vien_id", nullable = false)
    private UUID nhanVienId;

    @Column(name = "nam", nullable = false)
    private Integer nam;

    @Column(name = "so_ngay_duoc_huong", nullable = false, precision = 4, scale = 1)
    private BigDecimal soNgayDuocHuong;

    @Column(name = "so_ngay_da_dung", nullable = false, precision = 4, scale = 1)
    private BigDecimal soNgayDaDung = new BigDecimal("0.0");

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "so_ngay_con_lai", precision = 4, scale = 1, insertable = false, updatable = false)
    private BigDecimal soNgayConLai;

    public UUID getQuyPhepId() { return quyPhepId; }
    public void setQuyPhepId(UUID quyPhepId) { this.quyPhepId = quyPhepId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
    public BigDecimal getSoNgayDuocHuong() { return soNgayDuocHuong; }
    public void setSoNgayDuocHuong(BigDecimal soNgayDuocHuong) { this.soNgayDuocHuong = soNgayDuocHuong; }
    public BigDecimal getSoNgayDaDung() { return soNgayDaDung; }
    public void setSoNgayDaDung(BigDecimal soNgayDaDung) { this.soNgayDaDung = soNgayDaDung; }
    public BigDecimal getSoNgayConLai() { return soNgayConLai; }
}