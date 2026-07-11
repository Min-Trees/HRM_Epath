package com.company.hrm.common.security;

/**
 * Enum role tổng — dùng cho {@code @RequiresRole} cũ (giữ lại để tương thích).
 *
 * <p>T11 bổ sung 4 role mới theo {@code bosung.md}:
 * {@link #SYSTEM_ADMIN}, {@link #COMPANY_ADMIN}, {@link #HR_MANAGER}, {@link #ACCOUNTANT}.
 *
 * <p>Cấu hình thật (tên hiển thị, mô tả, permission set) lưu ở bảng {@code system.role}
 * + {@code system.role_permission} — enum này chỉ là cầu nối nhanh cho {@code @RequiresRole}.
 * Tương lai 1 task sẽ migrate toàn bộ controller sang {@code @RequiresPermission}.
 */
public enum Role {
    SYSTEM_ADMIN,
    COMPANY_ADMIN,
    HR_MANAGER,
    HR,
    MANAGER,
    BHXH_OFFICER,
    PAYROLL_ACCOUNTANT,
    ACCOUNTANT,
    EMPLOYEE
}