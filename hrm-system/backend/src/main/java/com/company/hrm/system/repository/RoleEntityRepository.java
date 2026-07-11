package com.company.hrm.system.repository;

import com.company.hrm.system.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(String code);
    List<RoleEntity> findAllByCodeIn(Collection<String> codes);
    boolean existsByCode(String code);
}