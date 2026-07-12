package com.company.hrm.training.repository;

import com.company.hrm.training.entity.DanhGiaSauDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DanhGiaSauDaoTaoRepository extends JpaRepository<DanhGiaSauDaoTao, UUID> {
    DanhGiaSauDaoTao findByDangKyId(UUID dangKyId);
}
