package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.CheckInRule;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.domain.enums.SubstituteMeRule;
import com.bida.employer.manager.domain.enums.SwapShiftRule;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RuleDTOResponse {

    private SubstituteMeRule substituteMeRule;
    private SwapShiftRule swapShiftRule;
    private CheckInRule checkInRule;
    private NotAssignedShiftRule notAssignedShiftRule;
    private int maxEmployeeShiftApplication;
}
