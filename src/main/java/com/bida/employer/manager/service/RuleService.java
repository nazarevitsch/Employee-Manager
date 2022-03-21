package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    public void validateRule(Rule rule) {
        if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.ALLOWED) && rule.getMaxEmployeeShiftApplication() <= 0) {
            throw new BadRequestException("If not assigned shifts are allowed, size of applications should be more then 0!");
        }
        if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.PROHIBITED) && rule.getMaxEmployeeShiftApplication() > 0) {
            throw new BadRequestException("If not assigned shifts are prohibited, size of applications should be 0!");
        }
        if (rule.getMaxEmployeeShiftApplication() < 0) {
            throw new BadRequestException("Size of applications can't be less then 0!");
        }
    }
}
