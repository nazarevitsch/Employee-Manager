package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationType;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class OrganizationCreateDTO {

    @NotNull(message = "Name can't be null")
    private String name;
    @NotNull(message = "Organization type can't be null")
    private OrganizationType organizationType;

    @NotNull(message = "User can't be null")
    private UserRegistrationDTO user;

    private RuleDTO rules;
}
