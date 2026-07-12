package com.company.hrm.training.repository;

import com.company.hrm.training.entity.DiemDanhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DiemDanhDaoTaoRepository extends JpaRepository<DiemDanhDaoTao, UUID> {
    List<DiemDanhDaoTao> findByDangKyId(UUID dangKyId);
}
