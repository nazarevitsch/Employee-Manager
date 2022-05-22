package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationSize;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class OrganizationCreateDTO {

    @NotEmpty(message = "Name can't be null or empty!")
    private String name;

    @NotNull(message = "Organization size can't be null!")
    private OrganizationSize organizationSize;

    @NotNull(message = "User can't be null!")
    @Valid
    private UserRegistrationDTO user;

    @NotNull(message = "Rule can't be empty!")
    @Valid
    private RuleDTO rules;
}
