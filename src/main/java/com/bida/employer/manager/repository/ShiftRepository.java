package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    @Query(value = "select * from shift where s_user_id is null and s_organization_id = :organizationId", nativeQuery = true)
    List<Shift> findAllUnassignedShiftByOrganizationId(@Param("organizationId") UUID organizationId);
}
