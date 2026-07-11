package com.company.hrm.system;

import java.util.UUID;

/**
 * Hằng số cho module system.
 */
public final class SystemConstants {

    /**
     * UUID cố định của company mặc định dùng cho seed data và migration backfill.
     * Toàn bộ NV/HĐ/Lương seed trước đây sẽ được gắn về company này.
     */
    public static final UUID DEFAULT_COMPANY_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final String DEFAULT_COMPANY_TAX_CODE = "0000000000";
    public static final String DEFAULT_COMPANY_NAME = "Default Company";

    /** Username cho SYSTEM_ADMIN seed. */
    public static final String SYSTEM_ADMIN_USERNAME = "admin";
    /** Username cho COMPANY_ADMIN seed. */
    public static final String COMPANY_ADMIN_USERNAME = "cadmin";

    private SystemConstants() {}
}