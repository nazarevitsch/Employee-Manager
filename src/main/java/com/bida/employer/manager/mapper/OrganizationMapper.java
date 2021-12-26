package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Organization;
import com.bida.employer.manager.domain.dto.OrganizationCreateDTO;
import com.bida.employer.manager.domain.dto.OrganizationDTOResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    private final ModelMapper modelMapper;

    public OrganizationMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Organization dtoToEntity(OrganizationCreateDTO organizationDTO) {
        return modelMapper.map(organizationDTO, Organization.class);
    }

    public OrganizationDTOResponse entityToResponseDto(Organization organization) {
        return modelMapper.map(organization, OrganizationDTOResponse.class);
    }
}
