package com.company.hrm.performance.repository;

import com.company.hrm.performance.entity.KpiAssignment;
import com.company.hrm.performance.entity.TrangThaiAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KpiAssignmentRepository extends JpaRepository<KpiAssignment, UUID> {
    List<KpiAssignment> findByNhanVienIdAndCycleId(UUID nhanVienId, UUID cycleId);
    List<KpiAssignment> findByCycleId(UUID cycleId);
    List<KpiAssignment> findByCycleIdAndTrangThai(UUID cycleId, TrangThaiAssignment trangThai);
}
