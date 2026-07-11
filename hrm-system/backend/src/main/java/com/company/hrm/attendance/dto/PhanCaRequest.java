package com.company.hrm.attendance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Gộp 3 mode phân ca:
 * <ul>
 *   <li><b>Single</b>: {@code nhanVienId} + {@code caId} + {@code ngayApDung}</li>
 *   <li><b>Bulk</b>: {@code nhanVienIds} + {@code caId} + {@code fromDate} + {@code toDate}</li>
 *   <li><b>Rotating</b>: {@code nhanVienIds} + {@code caIds} (nhiều ca) + {@code chuKy} + {@code fromDate} + {@code toDate}</li>
 * </ul>
 * Service sẽ detect mode dựa trên field nào có giá trị.
 */
public class PhanCaRequest {

    // Single
    private UUID nhanVienId;
    private UUID caId;
    private LocalDate ngayApDung;

    // Bulk / Rotating
    private List<UUID> nhanVienIds;
    private List<UUID> caIds;
    private LocalDate fromDate;
    private LocalDate toDate;

    /** Số ngày chu kỳ xoay vòng (cho rotating). Mặc định = {@code caIds.size()}. */
    @Min(1)
    private Integer chuKy;

    @Size(max = 300)
    private String ghiChu;

    /** Cho single: có cho phép ghi đè phân ca đã tồn tại? Mặc định false. */
    private Boolean override = false;

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public UUID getCaId() { return caId; }
    public void setCaId(UUID caId) { this.caId = caId; }
    public LocalDate getNgayApDung() { return ngayApDung; }
    public void setNgayApDung(LocalDate ngayApDung) { this.ngayApDung = ngayApDung; }
    public List<UUID> getNhanVienIds() { return nhanVienIds; }
    public void setNhanVienIds(List<UUID> nhanVienIds) { this.nhanVienIds = nhanVienIds; }
    @NotEmpty
    public List<UUID> getCaIds() { return caIds; }
    public void setCaIds(List<UUID> caIds) { this.caIds = caIds; }
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public Integer getChuKy() { return chuKy; }
    public void setChuKy(Integer chuKy) { this.chuKy = chuKy; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Boolean getOverride() { return override; }
    public void setOverride(Boolean override) { this.override = override; }
}
