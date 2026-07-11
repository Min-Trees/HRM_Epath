package com.company.hrm.attendance.repository;

import com.company.hrm.attendance.entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaLamViecRepository extends JpaRepository<CaLamViec, UUID> {

    Optional<CaLamViec> findByMaCa(String maCa);

    boolean existsByMaCa(String maCa);

    List<CaLamViec> findAllByOrderByMaCaAsc();

    List<CaLamViec> findAllByActiveOrderByMaCaAsc(boolean active);
}
