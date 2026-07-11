package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.QuyPhepNam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho bảng {@code timekeeping.quy_phep_nam}.
 * UNIQUE {@code (nhan_vien_id, nam)} được enforce ở DB và service.
 */
public interface QuyPhepNamRepository extends JpaRepository<QuyPhepNam, UUID> {

    boolean existsByNhanVienIdAndNam(UUID nhanVienId, Integer nam);

    Optional<QuyPhepNam> findByNhanVienIdAndNam(UUID nhanVienId, Integer nam);
}