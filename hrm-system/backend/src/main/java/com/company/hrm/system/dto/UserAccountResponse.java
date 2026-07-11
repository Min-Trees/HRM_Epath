package com.company.hrm.system.dto;

import com.company.hrm.system.entity.TrangThaiUserAccount;
import com.company.hrm.system.entity.UserAccount;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class UserAccountResponse {

    private UUID userId;
    private UUID companyId;
    private UUID employeeId;
    private String username;
    private String email;
    private List<String> roleCodes;
    private TrangThaiUserAccount trangThai;
    private OffsetDateTime lastLoginAt;
    private Instant createdAt;

    public static UserAccountResponse from(UserAccount u) {
        UserAccountResponse r = new UserAccountResponse();
        r.userId = u.getUserId();
        r.companyId = u.getCompanyId();
        r.employeeId = u.getEmployeeId();
        r.username = u.getUsername();
        r.email = u.getEmail();
        r.roleCodes = u.getRoleCodeList();
        r.trangThai = u.getTrangThai();
        r.lastLoginAt = u.getLastLoginAt();
        r.createdAt = u.getCreatedAt();
        return r;
    }

    public UUID getUserId() { return userId; }
    public UUID getCompanyId() { return companyId; }
    public UUID getEmployeeId() { return employeeId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<String> getRoleCodes() { return roleCodes; }
    public TrangThaiUserAccount getTrangThai() { return trangThai; }
    public OffsetDateTime getLastLoginAt() { return lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }
}