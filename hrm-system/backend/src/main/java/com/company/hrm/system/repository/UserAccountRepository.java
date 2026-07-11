package com.company.hrm.system.repository;

import com.company.hrm.system.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByEmail(String email);
    List<UserAccount> findByCompanyId(UUID companyId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}