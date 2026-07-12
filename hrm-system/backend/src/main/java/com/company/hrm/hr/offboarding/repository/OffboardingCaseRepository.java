package com.company.hrm.hr.offboarding.repository;

import com.company.hrm.hr.offboarding.entity.OffboardingCase;
import com.company.hrm.hr.offboarding.entity.TrangThaiOffboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OffboardingCaseRepository extends JpaRepository<OffboardingCase, UUID> {

    Optional<OffboardingCase> findByNhanVienIdAndTrangThaiNotIn(UUID nhanVienId, List<TrangThaiOffboarding> excluded);

    List<OffboardingCase> findByTrangThaiOrderByNgayChinhThucNghiDesc(TrangThaiOffboarding trangThai);

    @Query("SELECT c FROM OffboardingCase c WHERE c.trangThai NOT IN ('HOAN_THANH','HUY') " +
            "AND c.ngayChinhThucNghi >= :from AND c.ngayChinhThucNghi <= :to " +
            "ORDER BY c.ngayChinhThucNghi ASC")
    List<OffboardingCase> findUpcomingOffboardings(LocalDate from, LocalDate to);
}
