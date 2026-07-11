package com.company.hrm.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Base entity cho bảng append-only: chỉ có {@code created_at} / {@code created_by},
 * không có {@code updated_at} / {@code updated_by}. Dùng cho các bảng lịch sử
 * (biến động nhân sự, biến động BHXH, ...) mà quy tắc nghiệp vụ cấm UPDATE/DELETE.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedOnlyAuditEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
}
