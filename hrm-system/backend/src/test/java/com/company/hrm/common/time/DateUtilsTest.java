package com.company.hrm.common.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void workingDaysBetween_fullWeek() {
        LocalDate monday = LocalDate.of(2026, 7, 6);  // Mon
        LocalDate sunday = LocalDate.of(2026, 7, 12); // Sun
        assertEquals(5, DateUtils.workingDaysBetween(monday, sunday));
    }

    @Test
    void workingDaysBetween_singleDay() {
        assertEquals(1, DateUtils.workingDaysBetween(LocalDate.of(2026, 7, 6), LocalDate.of(2026, 7, 6)));
        assertEquals(0, DateUtils.workingDaysBetween(LocalDate.of(2026, 7, 11), LocalDate.of(2026, 7, 11)));
    }

    @Test
    void workingDaysBetween_invertedReturnsZero() {
        assertEquals(0, DateUtils.workingDaysBetween(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 6)));
    }

    @Test
    void isWorkingDay() {
        assertTrue(DateUtils.isWorkingDay(LocalDate.of(2026, 7, 6)));
        assertFalse(DateUtils.isWorkingDay(LocalDate.of(2026, 7, 11)));
        assertFalse(DateUtils.isWorkingDay(null));
    }

    @Test
    void daysBetween() {
        assertEquals(10, DateUtils.daysBetween(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 11)));
    }
}