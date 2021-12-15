package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.Organization;
import com.bida.employer.manager.domain.dto.OrganizationDTO;
import com.bida.employer.manager.domain.dto.OrganizationDTOResponse;
import com.bida.employer.manager.domain.dto.UserRegistrationDTO;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.OrganizationMapper;
import com.bida.employer.manager.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private UserService userService;

    public OrganizationDTOResponse create(OrganizationDTO organizationDTO) {
        UserRegistrationDTO registrationDTO = organizationDTO.getRegistrationDTO();
        userService.commonValidation(organizationDTO.getRegistrationDTO());

        Organization organization = organizationRepository.save(organizationMapper.dtoToEntity(organizationDTO));

        registrationDTO.setOrganizationId(organization.getId());
        userService.createOwner(registrationDTO);

        return organizationMapper.entityToResponseDto(organization);
    }

    public Organization checkOrganizationIsActive(UUID organizationId) {
        return Optional.of(findOrganizationById(organizationId))
                .orElseThrow(() -> new BadRequestException("Organization with id: " + organizationId + " doesn't active."));
    }

    public Organization findOrganizationById(UUID id) {
        return Optional.of(organizationRepository.findOrganizationById(id))
                .orElseThrow(() -> new NotFoundException("Organization with id: " + id + " doesn't exist."));
    }
}
