package com.company.hrm.system.repository;

import com.company.hrm.system.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<AuditLog> findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            UUID companyId, OffsetDateTime from, OffsetDateTime to);
    List<AuditLog> findByCompanyIdAndModuleOrderByCreatedAtDesc(UUID companyId, String module);
}