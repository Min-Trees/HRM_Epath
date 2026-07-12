package com.company.hrm.training.repository;

import com.company.hrm.training.entity.DangKyDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DangKyDaoTaoRepository extends JpaRepository<DangKyDaoTao, UUID> {
    List<DangKyDaoTao> findByLopHocId(UUID lopHocId);
    List<DangKyDaoTao> findByNhanVienId(UUID nhanVienId);
}
