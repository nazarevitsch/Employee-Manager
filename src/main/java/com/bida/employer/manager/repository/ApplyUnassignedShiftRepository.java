package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.ApplyUnassignedShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplyUnassignedShiftRepository extends JpaRepository<ApplyUnassignedShift, UUID> {

    List<ApplyUnassignedShift> findAllByUserId(UUID userId);

    void deleteAllByShiftIdAndUserId(UUID shiftId, UUID userId);
}
