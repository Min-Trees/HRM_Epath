package com.company.hrm.common.security;

import com.company.hrm.common.error.ForbiddenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextTest {

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void hasPermission_chuaSet_traFalse() {
        AuthContext.clear();
        assertFalse(AuthContext.hasPermission("nhan_vien.create"));
    }

    @Test
    void hasPermission_coTra_true() {
        UUID uid = UUID.randomUUID();
        AuthContext.set(uid, "HR", null, Set.of("nhan_vien.create", "leave.approve_cap1"));
        assertTrue(AuthContext.hasPermission("nhan_vien.create"));
        assertTrue(AuthContext.hasPermission("leave.approve_cap1"));
        assertFalse(AuthContext.hasPermission("cham_cong.chot"));
    }

    @Test
    void requirePermission_thieuThrow() {
        AuthContext.clear();
        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> AuthContext.requirePermission("nhan_vien.create"));
        assertEquals("PERMISSION_DENIED", ex.getCode());
    }

    @Test
    void csvToPermissions_emptyAndValid() {
        assertEquals(0, AuthContext.csvToPermissions(null).size());
        assertEquals(0, AuthContext.csvToPermissions("").size());
        assertEquals(0, AuthContext.csvToPermissions(" , ").size());
        Set<String> s = AuthContext.csvToPermissions("a,b, c ,, d");
        assertEquals(4, s.size());
        assertTrue(s.contains("a"));
        assertTrue(s.contains("b"));
        assertTrue(s.contains("c"));
        assertTrue(s.contains("d"));
    }

    @Test
    void csvToPermissions_doubleComma_skipEmpty() {
        // "a,,,b" → 4 tokens, 2 trống → 2 phần tử
        Set<String> s = AuthContext.csvToPermissions("a,,,b");
        assertEquals(2, s.size());
    }

    @Test
    void requireCompanyId_chuaSet_throws() {
        AuthContext.set(UUID.randomUUID(), "HR", null, Set.of());
        assertThrows(ForbiddenException.class, AuthContext::requireCompanyId);
    }

    @Test
    void requireCompanyId_coSet_tra() {
        UUID cid = UUID.randomUUID();
        AuthContext.set(UUID.randomUUID(), "HR", cid, Set.of());
        assertEquals(cid, AuthContext.requireCompanyId());
    }

    @Test
    void currentPermissions_emptyWhenAnonymous() {
        AuthContext.clear();
        assertEquals(0, AuthContext.currentPermissions().size());
    }
}