package com.company.hrm.common.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tiện ích tiền tệ — toàn bộ làm tròn HALF_UP, scale 2.
 * Không dùng double ở bất kỳ đâu trong tính lương / BHXH.
 */
public final class MoneyUtils {
    public static final int MONEY_SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private MoneyUtils() {}

    public static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(MONEY_SCALE) : value.setScale(MONEY_SCALE, ROUNDING);
    }

    public static BigDecimal money(double value) {
        return BigDecimal.valueOf(value).setScale(MONEY_SCALE, ROUNDING);
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return money(a).add(money(b));
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return money(a).subtract(money(b));
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return money(a).multiply(money(b));
    }

    public static BigDecimal percent(BigDecimal amount, BigDecimal percent) {
        // percent là tỷ lệ % (vd 10.5 = 10.5%)
        BigDecimal raw = money(amount).multiply(money(percent)).divide(money(100), ROUNDING);
        return money(raw);
    }

    public static boolean isPositive(BigDecimal v) {
        return v != null && v.signum() > 0;
    }
}