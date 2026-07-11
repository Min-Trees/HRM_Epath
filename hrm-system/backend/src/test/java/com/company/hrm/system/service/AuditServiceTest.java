package com.company.hrm.system.service;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.system.entity.AuditLog;
import com.company.hrm.system.repository.AuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    private AuditLogRepository repo;
    private AuditService service;

    @BeforeEach
    void setUp() {
        repo = mock(AuditLogRepository.class);
        service = new AuditService(repo);
        when(repo.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));
        AuthContext.clear();
    }

    @AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void record_anonymous_vanGhi() {
        AuthContext.set(null, null, null, Set.of());
        AuditLog saved = service.record("system", "CREATE", "Company",
                UUID.randomUUID(), null, "{\"foo\":\"bar\"}");
        assertNotNull(saved);
        assertEquals("system", saved.getModule());
        assertNull(saved.getUserId());
    }

    @Test
    void record_withUser() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        AuthContext.set(userId, "SYSTEM_ADMIN", companyId, Set.of());
        AuditLog saved = service.record("hr", "UPDATE", "NhanVien",
                UUID.randomUUID(), "{}", "{}");
        assertEquals(userId, saved.getUserId());
        assertEquals(companyId, saved.getCompanyId());
    }

    @Test
    void record_oldNew_null() {
        AuthContext.set(null, null, null, Set.of());
        AuditLog saved = service.record("hr", "DELETE", "NhanVien", null, null, null);
        assertNull(saved.getUserId());
        assertNull(saved.getOldValue());
        assertNull(saved.getNewValue());
    }
}