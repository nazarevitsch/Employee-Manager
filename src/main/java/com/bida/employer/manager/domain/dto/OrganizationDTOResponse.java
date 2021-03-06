package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.OrganizationSize;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrganizationDTOResponse {

    private UUID id;
    private String name;
    private LocalDateTime activeEndDate;
    private LocalDateTime creationDate;
    private OrganizationSize organizationSize;

    private RuleDTOResponse rules;
}
