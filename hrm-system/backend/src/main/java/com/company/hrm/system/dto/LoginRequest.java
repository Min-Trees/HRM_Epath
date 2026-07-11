package com.company.hrm.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body cho {@code POST /auth/login} và {@code POST /users/{id}/reset-password}.
 */
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}