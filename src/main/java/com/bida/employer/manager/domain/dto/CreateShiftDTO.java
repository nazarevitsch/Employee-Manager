package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class CreateShiftDTO {

    @NotNull
    @Valid
    private EmployeeDTO[] employees;
}
