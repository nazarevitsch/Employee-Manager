package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Organization findOrganizationById(UUID id);
}
