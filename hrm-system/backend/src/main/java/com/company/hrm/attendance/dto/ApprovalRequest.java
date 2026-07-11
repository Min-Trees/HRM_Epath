package com.company.hrm.attendance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Duyệt hoặc từ chối giải trình / bản ghi chấm công thủ công.
 */
public class ApprovalRequest {

    @NotNull
    private Boolean approve;

    @Size(max = 500)
    private String ghiChu;

    public Boolean getApprove() { return approve; }
    public void setApprove(Boolean approve) { this.approve = approve; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}