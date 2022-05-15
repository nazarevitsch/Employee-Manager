package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    @Modifying
    @Transactional
    @Query(value = "delete from shift where s_id in ('1') and s_user_id = ''", nativeQuery = true)
    public void deleteShiftsByIdsAndOrganizationId(@Param("") UUID organizationId, @Param("") List<UUID> shiftsId);
}
