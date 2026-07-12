package com.company.hrm.training.repository;

import com.company.hrm.training.entity.LopHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LopHocRepository extends JpaRepository<LopHoc, UUID> {
    List<LopHoc> findByChuongTrinhId(UUID chuongTrinhId);
}
