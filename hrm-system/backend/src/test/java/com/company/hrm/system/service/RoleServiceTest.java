package com.company.hrm.system.service;

import com.company.hrm.system.entity.Permission;
import com.company.hrm.system.entity.RoleEntity;
import com.company.hrm.system.repository.RoleEntityRepository;
import com.company.hrm.system.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private RoleEntityRepository roleRepo;
    private UserAccountRepository userRepo;
    private RoleService service;

    @BeforeEach
    void setUp() {
        roleRepo = mock(RoleEntityRepository.class);
        userRepo = mock(UserAccountRepository.class);
        service = new RoleService(roleRepo, userRepo);
    }

    private RoleEntity role(String code, String... permCodes) {
        RoleEntity r = new RoleEntity();
        r.setCode(code);
        Set<Permission> ps = new HashSet<>();
        for (String pc : permCodes) {
            Permission p = new Permission();
            p.setCode(pc);
            p.setModule(pc.split("\\.")[0]);
            ps.add(p);
        }
        r.setPermissions(ps);
        return r;
    }

    @Test
    void requireByCode_OK() {
        when(roleRepo.findByCode("HR_MANAGER")).thenReturn(Optional.of(role("HR_MANAGER")));
        assertEquals("HR_MANAGER", service.requireByCode("HR_MANAGER").getCode());
    }

    @Test
    void requireByCode_missing_throws() {
        when(roleRepo.findByCode("GHOST")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> service.requireByCode("GHOST"));
    }

    @Test
    void getPermissionsByRoleCodes_gop() {
        when(roleRepo.findAllByCodeIn(List.of("HR", "MANAGER")))
                .thenReturn(List.of(
                        role("HR", "nhan_vien.create", "nhan_vien.read"),
                        role("MANAGER", "leave.approve_cap1")));
        Set<String> perms = service.getPermissionsByRoleCodes(List.of("HR", "MANAGER"));
        assertEquals(3, perms.size());
        assertTrue(perms.contains("nhan_vien.create"));
        assertTrue(perms.contains("leave.approve_cap1"));
    }

    @Test
    void getPermissionsByRoleCodes_empty() {
        assertEquals(0, service.getPermissionsByRoleCodes(null).size());
        assertEquals(0, service.getPermissionsByRoleCodes(List.of()).size());
    }

    @Test
    void getPermissionsByUserId_notFound() {
        when(userRepo.findById(any())).thenReturn(Optional.empty());
        assertEquals(0, service.getPermissionsByUserId(UUID.randomUUID()).size());
    }

    @Test
    void getPermissionsByUserId_gop() {
        com.company.hrm.system.entity.UserAccount u = new com.company.hrm.system.entity.UserAccount();
        u.setUserId(UUID.randomUUID());
        u.setRoleCodeList(List.of("HR"));
        when(userRepo.findById(u.getUserId())).thenReturn(Optional.of(u));
        when(roleRepo.findAllByCodeIn(List.of("HR")))
                .thenReturn(List.of(role("HR", "nhan_vien.create")));

        Set<String> perms = service.getPermissionsByUserId(u.getUserId());
        assertTrue(perms.contains("nhan_vien.create"));
    }
}