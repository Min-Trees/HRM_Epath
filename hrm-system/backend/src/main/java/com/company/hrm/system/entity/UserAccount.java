package com.company.hrm.system.entity;

import com.company.hrm.common.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Tài khoản đăng nhập hệ thống HRM. Một {@code UserAccount} thuộc một
 * {@link Company} (multi-tenant) và có thể liên kết tuỳ chọn tới một
 * {@code hr.nhan_vien} (qua {@link #employeeId}).
 *
 * <p>Danh sách role lưu dưới dạng CSV trong cột {@link #roleCodes} — đủ cho
 * T11; khi cần mapping N-N phức tạp hơn (vd thêm cấp tenant-level override
 * permission) sẽ refactor sang bảng join {@code system.user_account_role}.
 */
@Entity
@Table(name = "user_account", schema = "system")
public class UserAccount extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "role_codes", nullable = false, length = 500)
    private String roleCodes = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiUserAccount trangThai = TrangThaiUserAccount.ACTIVE;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRoleCodes() { return roleCodes; }
    public void setRoleCodes(String roleCodes) { this.roleCodes = roleCodes; }

    /** Helper: tách CSV thành danh sách role code, loại bỏ token rỗng. */
    public List<String> getRoleCodeList() {
        if (roleCodes == null || roleCodes.isBlank()) return List.of();
        return Arrays.stream(roleCodes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public void setRoleCodeList(List<String> codes) {
        this.roleCodes = codes == null ? "" : String.join(",", codes);
    }
    public TrangThaiUserAccount getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiUserAccount trangThai) { this.trangThai = trangThai; }
    public OffsetDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(OffsetDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}