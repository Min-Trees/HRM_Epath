package com.company.hrm.system.dto;

import com.company.hrm.system.entity.TrangThaiUserAccount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class UserAccountRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    @Email
    private String email;

    /** Danh sách role code — phải tồn tại ở {@code system.role}. */
    private List<String> roleCodes;

    /** Có thể bỏ qua nếu SYSTEM_ADMIN tạo; COMPANY_ADMIN phải chỉ định. */
    private UUID companyId;

    /** Optional — liên kết sang nhân viên đã tồn tại. */
    private UUID employeeId;

    private TrangThaiUserAccount trangThai;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getRoleCodes() { return roleCodes; }
    public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }
    public TrangThaiUserAccount getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiUserAccount trangThai) { this.trangThai = trangThai; }
}