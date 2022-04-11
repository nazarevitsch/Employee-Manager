package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.CheckInRule;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.domain.enums.SubstituteMeRule;
import com.bida.employer.manager.domain.enums.SwapShiftRule;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class RuleDTO {

    @NotNull(message = "Substitute Me Rule can't be empty!")
    private SubstituteMeRule substituteMeRule;

    @NotNull(message = "Swap Shift Rule can't be empty!")
    private SwapShiftRule swapShiftRule;

    @NotNull(message = "Checkin Rule can't be empty!")
    private CheckInRule checkInRule;

    @NotNull(message = "Not Assigned Shift Rule can't be empty!")
    private NotAssignedShiftRule notAssignedShiftRule;

    private int maxEmployeeShiftApplication;
}
