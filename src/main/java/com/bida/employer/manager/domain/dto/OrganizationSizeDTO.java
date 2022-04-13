package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrganizationSizeDTO {

    @NotNull(message = "Organization type can't be null!")
    private OrganizationType organizationType;
}
