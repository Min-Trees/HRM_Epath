package com.company.hrm.system.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Quyền trong hệ thống. Format {@code code = "resource.action"} — ví dụ
 * {@code nhan_vien.create}, {@code leave.approve_cap1}.
 *
 * <p>Phân nhóm theo {@link #module} ({@code hr}, {@code timekeeping},
 * {@code social_ins}, {@code payroll}, {@code system}) cho dễ UI filter.
 */
@Entity
@Table(name = "permission", schema = "system")
public class Permission {

    @Id
    @GeneratedValue
    @Column(name = "permission_id")
    private UUID permissionId;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "module", nullable = false, length = 50)
    private String module;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    public UUID getPermissionId() { return permissionId; }
    public void setPermissionId(UUID permissionId) { this.permissionId = permissionId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}