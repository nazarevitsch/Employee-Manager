package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.CheckInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, UUID> {

    List<CheckInOut> findAllByShiftId(UUID shiftId);

    List<CheckInOut> findAllByShiftId(List<UUID> shiftIds);
}
