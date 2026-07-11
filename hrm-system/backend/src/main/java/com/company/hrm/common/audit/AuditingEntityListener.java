package com.company.hrm.common.audit;

import com.company.hrm.common.security.AuthContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.UUID;

public class AuditingEntityListener {

    @PrePersist
    public void onCreate(Object entity) {
        UUID user = AuthContext.currentUserIdOrNull();
        if (user != null) {
            try {
                entity.getClass().getMethod("setCreatedBy", UUID.class).invoke(entity, user);
            } catch (Exception ignored) { /* field optional */ }
        }
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        UUID user = AuthContext.currentUserIdOrNull();
        if (user != null) {
            try {
                entity.getClass().getMethod("setUpdatedBy", UUID.class).invoke(entity, user);
            } catch (Exception ignored) { /* field optional */ }
        }
    }
}