package com.company.hrm.hr.offboarding.dto;

import com.company.hrm.hr.offboarding.entity.LyDoNghiViec;
import com.company.hrm.hr.offboarding.entity.TrangThaiOffboarding;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class OffboardingCaseDto {

    private UUID caseId;
    private UUID nhanVienId;
    private String maNv;
    private String hoTen;

    @NotNull
    @Size(min = 1, max = 50)
    private String soQuyetDinh;

    @NotNull
    private LocalDate ngayQuyetDinh;

    @NotNull
    private LocalDate ngayNghiViecCuoi;

    @NotNull
    private LocalDate ngayChinhThucNghi;

    @NotNull
    private LyDoNghiViec lyDo;

    private String lyDoChiTiet;
    private TrangThaiOffboarding trangThai;
    private UUID nguoiTaoId;
    private UUID nguoiDuyetId;
    private LocalDateTime ngayDuyet;
    private String ghiChu;
    private UUID bhxhBienDongGiamId;
    private UUID quyetToanThueId;

    private Long tongTask;
    private Long taskHoanThanh;
    private Double tienDoPhanTram;

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
    public String getMaNv() { return maNv; }
    public void setMaNv(String maNv) { this.maNv = maNv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoQuyetDinh() { return soQuyetDinh; }
    public void setSoQuyetDinh(String soQuyetDinh) { this.soQuyetDinh = soQuyetDinh; }
    public LocalDate getNgayQuyetDinh() { return ngayQuyetDinh; }
    public void setNgayQuyetDinh(LocalDate ngayQuyetDinh) { this.ngayQuyetDinh = ngayQuyetDinh; }
    public LocalDate getNgayNghiViecCuoi() { return ngayNghiViecCuoi; }
    public void setNgayNghiViecCuoi(LocalDate ngayNghiViecCuoi) { this.ngayNghiViecCuoi = ngayNghiViecCuoi; }
    public LocalDate getNgayChinhThucNghi() { return ngayChinhThucNghi; }
    public void setNgayChinhThucNghi(LocalDate ngayChinhThucNghi) { this.ngayChinhThucNghi = ngayChinhThucNghi; }
    public LyDoNghiViec getLyDo() { return lyDo; }
    public void setLyDo(LyDoNghiViec lyDo) { this.lyDo = lyDo; }
    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public void setLyDoChiTiet(String lyDoChiTiet) { this.lyDoChiTiet = lyDoChiTiet; }
    public TrangThaiOffboarding getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiOffboarding trangThai) { this.trangThai = trangThai; }
    public UUID getNguoiTaoId() { return nguoiTaoId; }
    public void setNguoiTaoId(UUID nguoiTaoId) { this.nguoiTaoId = nguoiTaoId; }
    public UUID getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(UUID nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public UUID getBhxhBienDongGiamId() { return bhxhBienDongGiamId; }
    public void setBhxhBienDongGiamId(UUID bhxhBienDongGiamId) { this.bhxhBienDongGiamId = bhxhBienDongGiamId; }
    public UUID getQuyetToanThueId() { return quyetToanThueId; }
    public void setQuyetToanThueId(UUID quyetToanThueId) { this.quyetToanThueId = quyetToanThueId; }
    public Long getTongTask() { return tongTask; }
    public void setTongTask(Long tongTask) { this.tongTask = tongTask; }
    public Long getTaskHoanThanh() { return taskHoanThanh; }
    public void setTaskHoanThanh(Long taskHoanThanh) { this.taskHoanThanh = taskHoanThanh; }
    public Double getTienDoPhanTram() { return tienDoPhanTram; }
    public void setTienDoPhanTram(Double tienDoPhanTram) { this.tienDoPhanTram = tienDoPhanTram; }
}
