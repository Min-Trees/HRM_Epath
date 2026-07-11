package com.company.hrm.system.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation đánh dấu method service cần ghi audit log.
 * Áp dụng cho method service (tầng nghiệp vụ).
 *
 * <p>Ví dụ:
 * <pre>
 * &#64;Auditable(module = "timekeeping", action = "APPROVE_CAP2", entityType = "NghiPhep")
 * public NghiPhepResponse approveCap2(...) { ... }
 * </pre>
 *
 * <p>{@link AuditAspect} sẽ chạy sau khi method return thành công, gọi
 * {@link com.company.hrm.system.service.AuditService#record}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /** Module nghiệp vụ — vd {@code "hr"}, {@code "timekeeping"}, {@code "system"}. */
    String module();

    /** Action — vd {@code "CREATE"}, {@code "UPDATE"}, {@code "APPROVE"}, {@code "LOCK"}. */
    String action();

    /** Tên entity class (hoặc tên bảng) — vd {@code "NghiPhep"}. */
    String entityType();
}