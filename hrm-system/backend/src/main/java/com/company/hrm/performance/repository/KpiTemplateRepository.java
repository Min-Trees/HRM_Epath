package com.company.hrm.performance.repository;

import com.company.hrm.performance.entity.KpiTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KpiTemplateRepository extends JpaRepository<KpiTemplate, UUID> {
    List<KpiTemplate> findByIsActiveTrue();
}
