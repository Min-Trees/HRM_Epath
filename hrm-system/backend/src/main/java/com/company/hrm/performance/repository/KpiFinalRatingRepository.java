package com.company.hrm.performance.repository;

import com.company.hrm.performance.entity.KpiFinalRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KpiFinalRatingRepository extends JpaRepository<KpiFinalRating, UUID> {
    Optional<KpiFinalRating> findByAssignmentId(UUID assignmentId);
}
