package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.SubstituteMeRule;
import com.bida.employer.manager.domain.enums.SwapShiftRule;
import lombok.Data;

@Data
public class RuleDTO {

    private SubstituteMeRule substituteMeRule;
    private SwapShiftRule swapShiftRule;
}
