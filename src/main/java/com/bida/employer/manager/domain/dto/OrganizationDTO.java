package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationType;
import lombok.Data;

@Data
public class OrganizationDTO {

    private UserRegistrationDTO registrationDTO;

    private String name;
    private OrganizationType organizationType;
}
