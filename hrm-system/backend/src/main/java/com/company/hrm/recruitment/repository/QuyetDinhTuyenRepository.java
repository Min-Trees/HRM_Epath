package com.company.hrm.recruitment.repository;

import com.company.hrm.recruitment.entity.QuyetDinhTuyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuyetDinhTuyenRepository extends JpaRepository<QuyetDinhTuyen, UUID> {
    List<QuyetDinhTuyen> findByUngVienId(UUID ungVienId);
}
