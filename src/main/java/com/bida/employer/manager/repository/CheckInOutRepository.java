package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.CheckInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckInOutRepository extends JpaRepository<CheckInOut, UUID> {

    List<CheckInOut> findAllByShiftId(UUID shiftId);

    @Query(value = "select * from check_in_out where cio_shift_id in (:shiftIds)", nativeQuery = true)
    List<CheckInOut> findAllByShiftId(@Param("shiftIds") List<UUID> shiftIds);
}
