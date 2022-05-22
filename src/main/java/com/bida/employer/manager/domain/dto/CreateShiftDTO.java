package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateShiftDTO {

    private UUID[] userIds;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    @Valid
    private ShiftTimeDTO[] shifts;
}
