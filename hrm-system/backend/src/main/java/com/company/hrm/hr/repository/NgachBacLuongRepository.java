package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.NgachBacLuong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NgachBacLuongRepository extends JpaRepository<NgachBacLuong, UUID> {
    Optional<NgachBacLuong> findByMaNgach(String maNgach);
    boolean existsByMaNgach(String maNgach);
}