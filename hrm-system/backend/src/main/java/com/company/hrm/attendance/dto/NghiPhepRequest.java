package com.company.hrm.attendance.dto;

import com.company.hrm.attendance.entity.LoaiNghiPhep;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request tạo đơn nghỉ phép.
 *
 * <ul>
 *   <li>{@code OM} / {@code THAI_SAN}: yêu cầu {@code fileDinhKemUrl} (chứng từ).</li>
 *   <li>{@code PHEP_NAM}: kiểm tra quỹ (không trừ ngay, chỉ trừ khi DA_DUYET).</li>
 * </ul>
 *
 * <p>{@code soNgayNghi} được tính tự động từ lịch phân ca của NV — không cần client truyền.
 */
public class NghiPhepRequest {

    @NotNull
    private UUID nhanVienId;

    @NotNull
    private LoaiNghiPhep loaiNghiPhep;

    @NotNull
    private LocalDate tuNgay;

    @NotNull
    private LocalDate denNgay;

    @Size(max = 500)
    private String lyDo;

    @Size(max = 500)
    private String fileDinhKemUrl;

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public LoaiNghiPhep getLoaiNghiPhep() { return loaiNghiPhep; }
    public void setLoaiNghiPhep(LoaiNghiPhep loaiNghiPhep) { this.loaiNghiPhep = loaiNghiPhep; }
    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
}