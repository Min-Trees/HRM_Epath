package com.company.hrm.system.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Stub {@code PasswordEncoder} cho T11 — dùng SHA-256 + salt ngẫu nhiên.
 *
 * <p>TODO: khi có Spring Security, thay bằng {@code BCryptPasswordEncoder} để
 * tận dụng adaptive hashing + salt cost. Hiện chỉ cần mã hoá 1 chiều (verify
 * được hash từ raw password).
 *
 * <p>Format stored hash: {@code base64(salt):base64(sha256(salt + password))}.
 */
public class PasswordEncoder {

    private static final SecureRandom RNG = new SecureRandom();
    private static final int SALT_BYTES = 16;

    public String encode(String rawPassword) {
        if (rawPassword == null) throw new IllegalArgumentException("rawPassword must not be null");
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        byte[] hash = sha256(salt, rawPassword);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) return false;
        int idx = storedHash.indexOf(':');
        if (idx <= 0) return false;
        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(storedHash.substring(0, idx));
            expected = Base64.getDecoder().decode(storedHash.substring(idx + 1));
        } catch (IllegalArgumentException ex) {
            return false;
        }
        byte[] actual = sha256(salt, rawPassword);
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] sha256(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            md.update(password.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}