package com.company.hrm.common.security;

import com.company.hrm.common.error.ForbiddenException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Stub lấy user/role hiện tại từ header request. Đây là prototype —
 * sẽ thay bằng Spring Security + JWT khi tích hợp thật.
 *
 * <p>Header dùng (khi không có JWT):
 * <ul>
 *   <li>{@code X-User-Id}: UUID người dùng hiện tại (optional)</li>
 *   <li>{@code X-User-Role}: HR | MANAGER | ... (optional, tương thích ngược)</li>
 *   <li>{@code X-Company-Id}: tenant hiện tại (optional)</li>
 *   <li>{@code X-Permissions}: CSV permission code (optional, ví dụ {@code nhan_vien.create,leave.approve_cap1})</li>
 * </ul>
 *
 * <p>T11 mở rộng: thêm {@link #currentCompanyId()} và {@link #hasPermission(String)} để
 * hỗ trợ RBAC mới mà KHÔNG phá vỡ controller cũ đang dùng {@code @RequiresRole}.
 */
public final class AuthContext {

    public static final String HDR_USER_ID = "X-User-Id";
    public static final String HDR_USER_ROLE = "X-User-Role";
    public static final String HDR_COMPANY_ID = "X-Company-Id";
    public static final String HDR_PERMISSIONS = "X-Permissions";

    private static final ThreadLocal<UUID> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_ROLE = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_COMPANY = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> CURRENT_PERMS = new ThreadLocal<>();

    private AuthContext() {}

    /** Set đầy đủ — dùng khi login OK. */
    public static void set(UUID userId, String role, UUID companyId, Set<String> permissions) {
        CURRENT_USER.set(userId);
        CURRENT_ROLE.set(role);
        CURRENT_COMPANY.set(companyId);
        CURRENT_PERMS.set(permissions == null ? Collections.emptySet() : Set.copyOf(permissions));
    }

    /** Backwards-compatible setter (T09 + cũ). */
    public static void set(UUID userId, String role) {
        set(userId, role, null, Collections.emptySet());
    }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_ROLE.remove();
        CURRENT_COMPANY.remove();
        CURRENT_PERMS.remove();
    }

    public static UUID currentUserIdOrNull() {
        return CURRENT_USER.get();
    }

    public static UUID requireUserId() {
        UUID u = CURRENT_USER.get();
        if (u == null) throw new ForbiddenException("AUTH_REQUIRED", "Thiếu thông tin người dùng");
        return u;
    }

    public static String currentRoleOrNull() {
        return CURRENT_ROLE.get();
    }

    public static UUID currentCompanyIdOrNull() {
        return CURRENT_COMPANY.get();
    }

    /** Throws nếu chưa có companyId (vd register tenant thì OK anonymous). */
    public static UUID requireCompanyId() {
        UUID c = CURRENT_COMPANY.get();
        if (c == null) throw new ForbiddenException("TENANT_REQUIRED", "Thiếu thông tin doanh nghiệp");
        return c;
    }

    public static Set<String> currentPermissions() {
        Set<String> p = CURRENT_PERMS.get();
        return p == null ? Collections.emptySet() : p;
    }

    /** Check 1 permission code. Trả {@code false} nếu user không có trong cache. */
    public static boolean hasPermission(String code) {
        if (code == null) return false;
        return currentPermissions().contains(code);
    }

    /** Require 1 permission — throw {@code PERMISSION_DENIED} nếu thiếu. */
    public static void requirePermission(String code) {
        if (!hasPermission(code)) {
            throw new ForbiddenException("PERMISSION_DENIED",
                    "Thiếu quyền '" + code + "' để thực hiện thao tác này");
        }
    }

    public static boolean hasRole(String role) {
        if (role == null) return false;
        String current = CURRENT_ROLE.get();
        return current != null && current.equalsIgnoreCase(role);
    }

    public static void requireRole(Role... allowed) {
        String current = CURRENT_ROLE.get();
        if (current == null) {
            throw new ForbiddenException("AUTH_REQUIRED", "Thiếu thông tin người dùng");
        }
        for (Role r : allowed) {
            if (r.name().equals(current)) return;
        }
        throw new ForbiddenException("FORBIDDEN", "Vai trò '" + current + "' không được phép thực hiện thao tác này");
    }

    /** Convenience: chuyển CSV permissions string thành set. */
    public static Set<String> csvToPermissions(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptySet();
        Set<String> out = new HashSet<>();
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}