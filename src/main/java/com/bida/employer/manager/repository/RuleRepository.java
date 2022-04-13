package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {

    Rule findRuleByOrganizationId(UUID organizationId);
}
