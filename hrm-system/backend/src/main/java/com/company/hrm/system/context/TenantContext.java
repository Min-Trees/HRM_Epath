package com.company.hrm.system.context;

/**
 * Thread-local holder cho {@code companyId} hiện tại.
 *
 * <p>Được {@link com.company.hrm.common.security.AuthAspect} set cùng với
 * {@link com.company.hrm.common.security.AuthContext} cho mỗi request.
 * Service tầng nghiệp vụ sẽ đọc từ đây để filter tenant khi cần.
 *
 * <p>TODO khi chuyển sang Spring Security thật: thay bằng {@code @AuthenticationPrincipal}
 * + custom UserDetails có companyId.
 */
public final class TenantContext {

    private static final ThreadLocal<java.util.UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(java.util.UUID companyId) {
        CURRENT.set(companyId);
    }

    public static java.util.UUID currentCompanyIdOrNull() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}