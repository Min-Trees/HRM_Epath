package com.company.hrm.system.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void currentCompanyId_defaultNull() {
        TenantContext.clear();
        assertNull(TenantContext.currentCompanyIdOrNull());
    }

    @Test
    void set_get() {
        UUID id = UUID.randomUUID();
        TenantContext.set(id);
        assertEquals(id, TenantContext.currentCompanyIdOrNull());
    }

    @Test
    void clear_resets() {
        TenantContext.set(UUID.randomUUID());
        TenantContext.clear();
        assertNull(TenantContext.currentCompanyIdOrNull());
    }
}