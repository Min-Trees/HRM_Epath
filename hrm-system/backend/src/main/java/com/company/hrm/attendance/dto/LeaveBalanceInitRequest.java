package com.company.hrm.attendance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Request khởi tạo quỹ phép cho nhiều NV theo năm.
 */
public class LeaveBalanceInitRequest {

    @NotEmpty
    private List<UUID> nhanVienIds;

    @NotNull
    @Min(2000)
    private Integer nam;

    public List<UUID> getNhanVienIds() { return nhanVienIds; }
    public void setNhanVienIds(List<UUID> nhanVienIds) { this.nhanVienIds = nhanVienIds; }
    public Integer getNam() { return nam; }
    public void setNam(Integer nam) { this.nam = nam; }
}