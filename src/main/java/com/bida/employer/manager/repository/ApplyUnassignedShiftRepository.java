package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.ApplyUnassignedShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplyUnassignedShiftRepository extends JpaRepository<ApplyUnassignedShift, UUID> {
}
