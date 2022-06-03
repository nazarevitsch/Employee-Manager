package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.CheckInOutEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class CheckInOutDTO {

    @NotEmpty(message = "Check Time can't be null or empty!")
    private String note;

    @NotNull(message = "Check IN/OUT can't be null!")
    private CheckInOutEnum checkInOut;

    @NotNull(message = "Shift id can't be null!")
    private UUID shiftId;
}
