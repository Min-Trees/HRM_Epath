package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.PhanCa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhanCaRepository extends JpaRepository<PhanCa, UUID> {

    boolean existsByNhanVienIdAndNgayApDung(UUID nhanVienId, LocalDate ngayApDung);

    Optional<PhanCa> findByNhanVienIdAndNgayApDung(UUID nhanVienId, LocalDate ngayApDung);

    List<PhanCa> findByNhanVienIdAndNgayApDungBetweenOrderByNgayApDungAsc(
            UUID nhanVienId, LocalDate from, LocalDate to);

    boolean existsByCaId(UUID caId);

    List<PhanCa> findByCaId(UUID caId);
}
