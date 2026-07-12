package com.company.hrm.hr.offboarding.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * T14 - Task checklist thuoc mot offboarding case.
 *
 * <p>Moi case co nhieu task (TRA_TAI_SAN, BAN_GIAO_CONG_VIEC, ...) do
 * HR/IT/Manager/Payroll phu trach. Task co the danh dau KHONG_AP_DUNG neu
 * khong can thuc hien (vi du: NV khong giu laptop).
 */
@Entity
@Table(name = "offboarding_task", schema = "hr")
public class OffboardingTask {

    @Id
    @GeneratedValue
    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai_task", nullable = false, columnDefinition = "hr.loai_task_offboarding")
    private LoaiTaskOffboarding loaiTask;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "nguoi_phu_trach_id")
    private UUID nguoiPhuTrachId;

    @Column(name = "han_hoan_thanh")
    private LocalDate hanHoanThanh;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "hr.trang_thai_task")
    private TrangThaiTask trangThai = TrangThaiTask.CHUA_LAM;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Column(name = "nguoi_hoan_thanh_id")
    private UUID nguoiHoanThanhId;

    @Column(name = "file_dinh_kem_url", length = 500)
    private String fileDinhKemUrl;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "thu_tu", nullable = false)
    private Integer thuTu = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
