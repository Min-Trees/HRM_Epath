package com.company.hrm.hr.offboarding.dto;

import com.company.hrm.hr.offboarding.entity.LoaiTaskOffboarding;
import com.company.hrm.hr.offboarding.entity.TrangThaiTask;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class OffboardingTaskDto {

    private UUID taskId;
    private UUID caseId;

    @NotNull
    private LoaiTaskOffboarding loaiTask;

    private String moTa;
    private UUID nguoiPhuTrachId;
    private String nguoiPhuTrachTen;
    private LocalDate hanHoanThanh;

    @NotNull
    private TrangThaiTask trangThai = TrangThaiTask.CHUA_LAM;

    private LocalDateTime ngayHoanThanh;
    private UUID nguoiHoanThanhId;
    private String fileDinhKemUrl;
    private String ghiChu;
    private Integer thuTu;

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public LoaiTaskOffboarding getLoaiTask() { return loaiTask; }
    public void setLoaiTask(LoaiTaskOffboarding loaiTask) { this.loaiTask = loaiTask; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public UUID getNguoiPhuTrachId() { return nguoiPhuTrachId; }
    public void setNguoiPhuTrachId(UUID nguoiPhuTrachId) { this.nguoiPhuTrachId = nguoiPhuTrachId; }
    public String getNguoiPhuTrachTen() { return nguoiPhuTrachTen; }
    public void setNguoiPhuTrachTen(String nguoiPhuTrachTen) { this.nguoiPhuTrachTen = nguoiPhuTrachTen; }
    public LocalDate getHanHoanThanh() { return hanHoanThanh; }
    public void setHanHoanThanh(LocalDate hanHoanThanh) { this.hanHoanThanh = hanHoanThanh; }
    public TrangThaiTask getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiTask trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayHoanThanh() { return ngayHoanThanh; }
    public void setNgayHoanThanh(LocalDateTime ngayHoanThanh) { this.ngayHoanThanh = ngayHoanThanh; }
    public UUID getNguoiHoanThanhId() { return nguoiHoanThanhId; }
    public void setNguoiHoanThanhId(UUID nguoiHoanThanhId) { this.nguoiHoanThanhId = nguoiHoanThanhId; }
    public String getFileDinhKemUrl() { return fileDinhKemUrl; }
    public void setFileDinhKemUrl(String fileDinhKemUrl) { this.fileDinhKemUrl = fileDinhKemUrl; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Integer getThuTu() { return thuTu; }
    public void setThuTu(Integer thuTu) { this.thuTu = thuTu; }
}
