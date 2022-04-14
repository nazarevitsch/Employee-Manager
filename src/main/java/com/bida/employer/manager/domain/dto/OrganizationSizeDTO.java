package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationSize;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrganizationSizeDTO {

    @NotNull(message = "Organization size can't be null!")
    private OrganizationSize organizationSize;
}
