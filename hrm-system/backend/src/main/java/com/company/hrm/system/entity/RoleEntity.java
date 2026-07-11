package com.company.hrm.system.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Vai trò trong hệ thống. Tên Java đặt là {@code RoleEntity} để tránh trùng với
 * enum {@link com.company.hrm.common.security.Role} cũ (giữ lại cho
 * {@code @RequiresRole}).
 *
 * <p>Một role gán nhiều permission qua bảng {@code system.role_permission}.
 */
@Entity
@Table(name = "role", schema = "system")
public class RoleEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "ten_role", nullable = false, length = 200)
    private String tenRole;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission",
            schema = "system",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTenRole() { return tenRole; }
    public void setTenRole(String tenRole) { this.tenRole = tenRole; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public boolean isSystem() { return isSystem; }
    public void setSystem(boolean system) { isSystem = system; }
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}