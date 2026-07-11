package com.company.hrm.common.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyUtilsTest {

    @Test
    void money_roundsHalfUpAtScale2() {
        BigDecimal v = new BigDecimal("12345.675");
        assertEquals(new BigDecimal("12345.68"), MoneyUtils.money(v));
    }

    @Test
    void money_fromDouble() {
        assertEquals(new BigDecimal("1.00"), MoneyUtils.money(1.0));
        assertEquals(new BigDecimal("1.50"), MoneyUtils.money(1.5));
    }

    @Test
    void percent_calculatesExactly() {
        BigDecimal amount = new BigDecimal("10000000");
        BigDecimal percent = new BigDecimal("10.5");
        assertEquals(new BigDecimal("1050000.00"), MoneyUtils.percent(amount, percent));
    }

    @Test
    void percent_zero() {
        assertEquals(new BigDecimal("0.00"), MoneyUtils.percent(BigDecimal.ZERO, new BigDecimal("21.5")));
    }

    @Test
    void addAndSubtract_keepScale() {
        BigDecimal a = new BigDecimal("1234567.89");
        BigDecimal b = new BigDecimal("0.11");
        assertEquals(new BigDecimal("1234568.00"), MoneyUtils.add(a, b));
        assertEquals(new BigDecimal("1234567.78"), MoneyUtils.subtract(a, b));
    }

    @Test
    void isPositive() {
        assertTrue(MoneyUtils.isPositive(new BigDecimal("0.01")));
        assertFalse(MoneyUtils.isPositive(BigDecimal.ZERO));
        assertFalse(MoneyUtils.isPositive(new BigDecimal("-1")));
        assertFalse(MoneyUtils.isPositive(null));
    }
}