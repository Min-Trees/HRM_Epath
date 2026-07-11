package com.company.hrm.system.service;

import com.company.hrm.common.security.AuthContext;
import com.company.hrm.system.context.TenantContext;
import com.company.hrm.system.entity.AuditLog;
import com.company.hrm.system.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Ghi log audit. Được {@code com.company.hrm.system.audit.AuditAspect} gọi.
 *
 * <p>Nếu chưa có user đăng nhập (vd: flow register tenant) thì vẫn ghi log
 * với {@code userId = null}.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AuditLog record(String module, String action, String entityType, UUID entityId,
                           String oldValue, String newValue) {
        AuditLog entry = new AuditLog();
        entry.setUserId(AuthContext.currentUserIdOrNull());
        entry.setCompanyId(AuthContext.currentCompanyIdOrNull() != null
                ? AuthContext.currentCompanyIdOrNull()
                : TenantContext.currentCompanyIdOrNull());
        entry.setModule(module);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setOldValue(oldValue);
        entry.setNewValue(newValue);
        entry.setCreatedAt(OffsetDateTime.now());
        AuditLog saved = repo.save(entry);
        log.debug("Audit log: module={} action={} entity={}#{}", module, action, entityType, entityId);
        return saved;
    }
}