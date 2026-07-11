package com.company.hrm.hr.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AssignManagerRequest {
    /** null = bỏ gán trưởng bộ phận */
    @NotNull
    private UUID nhanVienId;

    public UUID getNhanVienId() { return nhanVienId; }
    public void setNhanVienId(UUID nhanVienId) { this.nhanVienId = nhanVienId; }
}