package com.company.hrm.system.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    private final PasswordEncoder enc = new PasswordEncoder();

    @Test
    void encode_returnsNotNull() {
        assertNotNull(enc.encode("hello"));
    }

    @Test
    void encode_differentSaltEachTime() {
        String h1 = enc.encode("hello");
        String h2 = enc.encode("hello");
        assertNotEquals(h1, h2, "Mỗi lần encode phải ra salt khác nhau");
    }

    @Test
    void matches_correctPassword() {
        String hash = enc.encode("admin123");
        assertTrue(enc.matches("admin123", hash));
    }

    @Test
    void matches_wrongPassword_false() {
        String hash = enc.encode("admin123");
        assertFalse(enc.matches("admin124", hash));
    }

    @Test
    void matches_malformedHash_false() {
        assertFalse(enc.matches("anything", "not-a-valid-hash"));
        assertFalse(enc.matches("anything", null));
        assertFalse(enc.matches(null, "abc:def"));
    }

    @Test
    void encode_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> enc.encode(null));
    }
}