package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EmployeeDTO {

    @NotNull
    private UUID userId;

    @NotNull
    @Valid
    private ShiftDTO[] shifts;

    @NotNull
    private String title;

    @NotNull
    private String description;
}
