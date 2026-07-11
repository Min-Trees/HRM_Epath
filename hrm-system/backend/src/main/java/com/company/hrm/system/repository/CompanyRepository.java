package com.company.hrm.system.repository;

import com.company.hrm.system.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByMaSoThue(String maSoThue);
    Optional<Company> findByMaSoThue(String maSoThue);
    List<Company> findByTrangThai(com.company.hrm.system.entity.TrangThaiCompany trangThai);
}