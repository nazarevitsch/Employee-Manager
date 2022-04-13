package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    public Rule findRuleByOrganizationId(UUID organizationId) {
        return Optional.ofNullable(ruleRepository.findRuleByOrganizationId(organizationId))
                .orElseThrow(() -> new NotFoundException("Rule for organization with id: " + organizationId + " wasn't found!"));
    }

    public void validateRule(Rule rule) {
        if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.ALLOWED) && rule.getMaxEmployeeShiftApplication() <= 0) {
            throw new BadRequestException("If not assigned shifts are allowed, size of applications should be more then 0!");
        }
        if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.PROHIBITED) && rule.getMaxEmployeeShiftApplication() != 0) {
            throw new BadRequestException("If not assigned shifts are prohibited, size of applications should be 0!");
        }
    }
}
