package com.company.hrm.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation kiểm tra permission (RBAC mới). Áp dụng cho controller method
 * (hoặc cả class).
 *
 * <p>Đọc qua {@link AuthContext#hasPermission(String)} — cache được set lúc
 * login. Nếu user có ít nhất 1 permission trong {@link #value()} → cho phép.
 *
 * <p>T11 thêm mới song song với {@link RequiresRole} cũ — task sau sẽ migrate
 * từng controller sang permission khi chuẩn hoá.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /** Danh sách permission code cần có (chỉ cần 1 là đủ). */
    String[] value();
}