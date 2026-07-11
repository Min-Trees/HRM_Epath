package com.company.hrm.system.config;

import com.company.hrm.system.SystemConstants;
import com.company.hrm.system.entity.UserAccount;
import com.company.hrm.system.repository.UserAccountRepository;
import com.company.hrm.system.security.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Sau khi Flyway chạy V6 (chèn user với password_hash placeholder), component này
 * tự động encode lại password gốc để có thể login.
 *
 * <p>Dev-only. Tương lai sẽ thay bằng Spring Security + BCrypt.
 */
@Component
public class SystemDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemDataInitializer.class);

    private static final String PLACEHOLDER = "__PLACEHOLDER_RESET_BY_INITIALIZER__";

    private final UserAccountRepository repo;
    private final PasswordEncoder passwordEncoder;

    public SystemDataInitializer(UserAccountRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        resetIfPlaceholder(SystemConstants.SYSTEM_ADMIN_USERNAME, "admin123");
        resetIfPlaceholder(SystemConstants.COMPANY_ADMIN_USERNAME, "cadmin123");
    }

    private void resetIfPlaceholder(String username, String rawPassword) {
        UUID id = repo.findByUsername(username)
                .filter(u -> PLACEHOLDER.equals(u.getPasswordHash()))
                .map(UserAccount::getUserId)
                .orElse(null);
        if (id == null) return;
        UserAccount u = repo.findById(id).orElseThrow();
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        repo.save(u);
        log.info("Reset password cho user '{}' (V6 placeholder)", username);
    }
}