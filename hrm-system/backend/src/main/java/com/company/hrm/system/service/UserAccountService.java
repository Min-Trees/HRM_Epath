package com.company.hrm.system.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.system.dto.LoginRequest;
import com.company.hrm.system.dto.LoginResponse;
import com.company.hrm.system.dto.UserAccountRequest;
import com.company.hrm.system.dto.UserAccountResponse;
import com.company.hrm.system.entity.RoleEntity;
import com.company.hrm.system.entity.TrangThaiUserAccount;
import com.company.hrm.system.entity.UserAccount;
import com.company.hrm.system.repository.UserAccountRepository;
import com.company.hrm.system.security.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Nghiệp vụ quản lý tài khoản người dùng.
 *
 * <p>Stub BCrypt/SHA-256 ở {@link PasswordEncoder}. Tương lai sẽ thay bằng Spring Security.
 * KHÔNG xoá user (chỉ {@link #lock}/{@link #unlock}) — giữ lịch sử login.
 */
@Service
public class UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository repo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository repo,
                              RoleService roleService,
                              PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccountResponse create(UserAccountRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            throw new BusinessException("USERNAME_DUPLICATE",
                    "Username '" + req.getUsername() + "' đã tồn tại");
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()
                && repo.existsByEmail(req.getEmail())) {
            throw new BusinessException("EMAIL_DUPLICATE",
                    "Email '" + req.getEmail() + "' đã tồn tại");
        }
        if (req.getCompanyId() == null) {
            throw new BusinessException("COMPANY_REQUIRED",
                    "Thiếu companyId khi tạo tài khoản");
        }
        if (req.getRoleCodes() == null || req.getRoleCodes().isEmpty()) {
            throw new BusinessException("ROLE_REQUIRED",
                    "Cần chỉ định ít nhất 1 role code");
        }
        // Validate role tồn tại
        List<RoleEntity> roles = roleService.findAllByCodes(req.getRoleCodes());
        if (roles.size() != req.getRoleCodes().size()) {
            throw new BusinessException("ROLE_NOT_FOUND",
                    "Một số role không tồn tại: " + req.getRoleCodes());
        }
        UserAccount u = new UserAccount();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setCompanyId(req.getCompanyId());
        u.setEmployeeId(req.getEmployeeId());
        u.setRoleCodeList(req.getRoleCodes());
        u.setTrangThai(req.getTrangThai() != null ? req.getTrangThai() : TrangThaiUserAccount.ACTIVE);

        UserAccount saved = repo.save(u);
        log.info("Tạo user '{}' (company {}, roles {})",
                saved.getUsername(), saved.getCompanyId(), saved.getRoleCodes());
        return UserAccountResponse.from(saved);
    }

    @Transactional
    public UserAccountResponse update(UUID userId, UserAccountRequest req) {
        UserAccount u = requireById(userId);
        if (req.getEmail() != null && !req.getEmail().isBlank()
                && !req.getEmail().equals(u.getEmail())
                && repo.existsByEmail(req.getEmail())) {
            throw new BusinessException("EMAIL_DUPLICATE",
                    "Email '" + req.getEmail() + "' đã tồn tại");
        }
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getRoleCodes() != null && !req.getRoleCodes().isEmpty()) {
            u.setRoleCodeList(req.getRoleCodes());
        }
        if (req.getTrangThai() != null) u.setTrangThai(req.getTrangThai());
        if (req.getEmployeeId() != null) u.setEmployeeId(req.getEmployeeId());
        log.info("Cập nhật user '{}'", u.getUsername());
        return UserAccountResponse.from(repo.save(u));
    }

    @Transactional
    public UserAccountResponse lock(UUID userId) {
        UserAccount u = requireById(userId);
        u.setTrangThai(TrangThaiUserAccount.LOCKED);
        log.info("Khóa user '{}'", u.getUsername());
        return UserAccountResponse.from(repo.save(u));
    }

    @Transactional
    public UserAccountResponse unlock(UUID userId) {
        UserAccount u = requireById(userId);
        u.setTrangThai(TrangThaiUserAccount.ACTIVE);
        log.info("Mở khóa user '{}'", u.getUsername());
        return UserAccountResponse.from(repo.save(u));
    }

    @Transactional
    public UserAccountResponse resetPassword(UUID userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("PASSWORD_TOO_SHORT", "Mật khẩu tối thiểu 6 ký tự");
        }
        UserAccount u = requireById(userId);
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        log.info("Reset password user '{}'", u.getUsername());
        return UserAccountResponse.from(repo.save(u));
    }

    /**
     * Login stub — verify username + password, cập nhật {@code lastLoginAt}, trả LoginResponse
     * gồm role + permissions cache.
     *
     * @throws BusinessException {@code AUTH_INVALID} khi sai credential, {@code USER_LOCKED} khi bị khoá.
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        UserAccount u = repo.findByUsername(req.getUsername()).orElseThrow(() ->
                new BusinessException("AUTH_INVALID", "Username hoặc password không đúng"));

        if (u.getTrangThai() != TrangThaiUserAccount.ACTIVE) {
            throw new BusinessException("USER_LOCKED",
                    "Tài khoản '" + u.getUsername() + "' đang bị "
                            + (u.getTrangThai() == TrangThaiUserAccount.LOCKED ? "khóa" : "chờ kích hoạt"));
        }
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BusinessException("AUTH_INVALID", "Username hoặc password không đúng");
        }
        u.setLastLoginAt(OffsetDateTime.now());
        repo.save(u);

        Set<String> roleCodes = u.getRoleCodeList().stream().collect(java.util.stream.Collectors.toUnmodifiableSet());
        Set<String> permissions = roleService.getPermissionsByRoleCodes(roleCodes);

        log.info("User '{}' login OK (company {}, {} permissions)",
                u.getUsername(), u.getCompanyId(), permissions.size());
        // Token = base64(userId|ts) placeholder
        String token = java.util.Base64.getEncoder().encodeToString(
                (u.getUserId() + "|" + System.currentTimeMillis()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return new LoginResponse(token, u.getUserId(), u.getCompanyId(),
                u.getUsername(), roleCodes, permissions);
    }

    @Transactional(readOnly = true)
    public UserAccountResponse getById(UUID userId) {
        return UserAccountResponse.from(requireById(userId));
    }

    @Transactional(readOnly = true)
    public List<UserAccountResponse> listByCompany(UUID companyId) {
        return repo.findByCompanyId(companyId).stream()
                .map(UserAccountResponse::from).toList();
    }

    public UserAccount requireById(UUID userId) {
        return repo.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("USER_NOT_FOUND", "Không tìm thấy tài khoản"));
    }

    /** Internal: dùng cho seed khi cần ép role codes. */
    @Transactional
    public UserAccount forceCreateForSeed(String username, UUID companyId, List<String> roleCodes,
                                          String rawPassword) {
        if (repo.existsByUsername(username)) {
            return repo.findByUsername(username).orElseThrow();
        }
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setCompanyId(companyId);
        u.setRoleCodeList(roleCodes);
        u.setTrangThai(TrangThaiUserAccount.ACTIVE);
        return repo.save(u);
    }
}