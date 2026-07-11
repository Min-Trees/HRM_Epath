package com.company.hrm.hr.repository;

import com.company.hrm.hr.entity.PhongBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhongBanRepository extends JpaRepository<PhongBan, UUID> {
    Optional<PhongBan> findByMaPhongBan(String maPhongBan);
    List<PhongBan> findAllByOrderByCapDoAscMaPhongBanAsc();
    List<PhongBan> findByPhongBanChaId(UUID phongBanChaId);
    long countByPhongBanChaId(UUID phongBanChaId);
    boolean existsByTruongBoPhanId(UUID nhanVienId);
}