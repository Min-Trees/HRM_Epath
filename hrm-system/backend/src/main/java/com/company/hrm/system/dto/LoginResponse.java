package com.company.hrm.system.dto;

import java.util.Set;
import java.util.UUID;

/**
 * Stub JWT — chỉ trả thông tin cần thiết cho client (token giả, không verify).
 * Task sau sẽ thay bằng JWT ký bằng khóa bí mật.
 */
public class LoginResponse {

    private final String token;            // JWT placeholder
    private final UUID userId;
    private final UUID companyId;
    private final String username;
    private final Set<String> roleCodes;
    private final Set<String> permissions;

    public LoginResponse(String token, UUID userId, UUID companyId, String username,
                         Set<String> roleCodes, Set<String> permissions) {
        this.token = token;
        this.userId = userId;
        this.companyId = companyId;
        this.username = username;
        this.roleCodes = roleCodes;
        this.permissions = permissions;
    }

    public String getToken() { return token; }
    public UUID getUserId() { return userId; }
    public UUID getCompanyId() { return companyId; }
    public String getUsername() { return username; }
    public Set<String> getRoleCodes() { return roleCodes; }
    public Set<String> getPermissions() { return permissions; }
}