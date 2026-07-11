package com.company.hrm.system.service;

import com.company.hrm.common.error.BusinessException;
import com.company.hrm.common.error.ResourceNotFoundException;
import com.company.hrm.system.dto.LoginRequest;
import com.company.hrm.system.dto.UserAccountRequest;
import com.company.hrm.system.dto.UserAccountResponse;
import com.company.hrm.system.entity.RoleEntity;
import com.company.hrm.system.entity.TrangThaiUserAccount;
import com.company.hrm.system.entity.UserAccount;
import com.company.hrm.system.repository.UserAccountRepository;
import com.company.hrm.system.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAccountServiceTest {

    private UserAccountRepository userRepo;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    private UserAccountService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserAccountRepository.class);
        roleService = mock(RoleService.class);
        passwordEncoder = new PasswordEncoder();
        service = new UserAccountService(userRepo, roleService, passwordEncoder);

        when(userRepo.save(any(UserAccount.class))).thenAnswer(inv -> {
            UserAccount u = inv.getArgument(0);
            if (u.getUserId() == null) u.setUserId(UUID.randomUUID());
            return u;
        });
    }

    private UserAccountRequest req(String username, String password, List<String> roles) {
        UserAccountRequest r = new UserAccountRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setEmail(username + "@test.local");
        r.setCompanyId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        r.setRoleCodes(roles);
        return r;
    }

    private RoleEntity roleStub(String code) {
        RoleEntity r = new RoleEntity();
        r.setCode(code);
        r.setPermissions(new HashSet<>());
        return r;
    }

    // ---------------- create ----------------

    @Test
    void create_thanhCong() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(roleService.findAllByCodes(List.of("EMPLOYEE"))).thenReturn(List.of(roleStub("EMPLOYEE")));

        UserAccountResponse r = service.create(req("alice", "secret123", List.of("EMPLOYEE")));
        assertEquals("alice", r.getUsername());
        assertEquals(TrangThaiUserAccount.ACTIVE, r.getTrangThai());
        assertTrue(r.getRoleCodes().contains("EMPLOYEE"));
    }

    @Test
    void create_usernameTrung_throws() {
        when(userRepo.existsByUsername("alice")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("alice", "secret123", List.of("EMPLOYEE"))));
        assertEquals("USERNAME_DUPLICATE", ex.getCode());
    }

    @Test
    void create_emailTrung_throws() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(userRepo.existsByEmail("alice@test.local")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("alice", "secret123", List.of("EMPLOYEE"))));
        assertEquals("EMAIL_DUPLICATE", ex.getCode());
    }

    @Test
    void create_thieuCompany_throws() {
        UserAccountRequest r = req("alice", "secret123", List.of("EMPLOYEE"));
        r.setCompanyId(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.create(r));
        assertEquals("COMPANY_REQUIRED", ex.getCode());
    }

    @Test
    void create_thieuRole_throws() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("alice", "secret123", List.of())));
        assertEquals("ROLE_REQUIRED", ex.getCode());
    }

    @Test
    void create_roleKhongTonTai_throws() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(roleService.findAllByCodes(List.of("GHOST_ROLE"))).thenReturn(List.of());
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(req("alice", "secret123", List.of("GHOST_ROLE"))));
        assertEquals("ROLE_NOT_FOUND", ex.getCode());
    }

    // ---------------- lock/unlock ----------------

    @Test
    void lock_thanhCong() {
        UUID id = UUID.randomUUID();
        UserAccount u = new UserAccount();
        u.setUserId(id);
        u.setUsername("alice");
        u.setTrangThai(TrangThaiUserAccount.ACTIVE);
        when(userRepo.findById(id)).thenReturn(Optional.of(u));

        UserAccountResponse r = service.lock(id);
        assertEquals(TrangThaiUserAccount.LOCKED, r.getTrangThai());
    }

    @Test
    void unlock_thanhCong() {
        UUID id = UUID.randomUUID();
        UserAccount u = new UserAccount();
        u.setUserId(id);
        u.setUsername("alice");
        u.setTrangThai(TrangThaiUserAccount.LOCKED);
        when(userRepo.findById(id)).thenReturn(Optional.of(u));

        assertEquals(TrangThaiUserAccount.ACTIVE, service.unlock(id).getTrangThai());
    }

    // ---------------- resetPassword ----------------

    @Test
    void resetPassword_thanhCong() {
        UUID id = UUID.randomUUID();
        UserAccount u = new UserAccount();
        u.setUserId(id);
        u.setUsername("alice");
        u.setPasswordHash(passwordEncoder.encode("old123"));
        when(userRepo.findById(id)).thenReturn(Optional.of(u));

        UserAccountResponse r = service.resetPassword(id, "newSecret123");
        assertNotNull(r);
        assertTrue(passwordEncoder.matches("newSecret123", u.getPasswordHash()));
        assertFalse(passwordEncoder.matches("old123", u.getPasswordHash()));
    }

    @Test
    void resetPassword_quaNgan_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resetPassword(UUID.randomUUID(), "12345"));
        assertEquals("PASSWORD_TOO_SHORT", ex.getCode());
    }

    // ---------------- login ----------------

    @Test
    void login_OK() {
        UserAccount u = new UserAccount();
        u.setUserId(UUID.randomUUID());
        u.setUsername("alice");
        u.setCompanyId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        u.setPasswordHash(passwordEncoder.encode("secret123"));
        u.setRoleCodeList(List.of("EMPLOYEE"));
        u.setTrangThai(TrangThaiUserAccount.ACTIVE);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));
        when(roleService.getPermissionsByRoleCodes(Set.of("EMPLOYEE"))).thenReturn(Set.of("nhan_vien.read"));

        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("secret123");
        var resp = service.login(req);
        assertEquals("alice", resp.getUsername());
        assertTrue(resp.getPermissions().contains("nhan_vien.read"));
    }

    @Test
    void login_saiPassword_throws() {
        UserAccount u = new UserAccount();
        u.setUsername("alice");
        u.setPasswordHash(passwordEncoder.encode("secret123"));
        u.setTrangThai(TrangThaiUserAccount.ACTIVE);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("wrongPass");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(req));
        assertEquals("AUTH_INVALID", ex.getCode());
    }

    @Test
    void login_userNotFound_throws() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest();
        req.setUsername("ghost");
        req.setPassword("anything123");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(req));
        assertEquals("AUTH_INVALID", ex.getCode());
    }

    @Test
    void login_userLocked_throws() {
        UserAccount u = new UserAccount();
        u.setUsername("alice");
        u.setPasswordHash(passwordEncoder.encode("secret123"));
        u.setTrangThai(TrangThaiUserAccount.LOCKED);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));

        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("secret123");
        BusinessException ex = assertThrows(BusinessException.class, () -> service.login(req));
        assertEquals("USER_LOCKED", ex.getCode());
    }

    // ---------------- requireById ----------------

    @Test
    void requireById_notFound_throws() {
        when(userRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
    }
}