package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.ApplyUnassignedShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ApplyUnassignedShiftRepository extends JpaRepository<ApplyUnassignedShift, UUID> {

    List<ApplyUnassignedShift> findAllByUserId(UUID userId);

    void deleteAllByShiftIdAndUserId(UUID shiftId, UUID userId);

    @Modifying
    @Transactional
    @Query(value = "delete from apply_unassigned_shift where aus_shift_id = :shiftId", nativeQuery = true)
    void deleteApplyingByShiftId(@Param("shiftId") UUID shiftId);
}
