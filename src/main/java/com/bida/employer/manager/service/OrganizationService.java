package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.Organization;
import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.dto.OrganizationCreateDTO;
import com.bida.employer.manager.domain.dto.OrganizationDTOResponse;
import com.bida.employer.manager.domain.dto.UserCreateDTO;
import com.bida.employer.manager.domain.dto.UserRegistrationDTO;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.OrganizationMapper;
import com.bida.employer.manager.mapper.RuleMapper;
import com.bida.employer.manager.repository.OrganizationRepository;
import com.bida.employer.manager.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private UserService userService;
    @Autowired
    private RuleRepository ruleRepository;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private OrganizationRepository organizationRepository;


    public OrganizationDTOResponse create(OrganizationCreateDTO organizationDTO) {
        UserRegistrationDTO user = organizationDTO.getUser();
        userService.ownerCreationValidation(user);

        Organization organization = organizationRepository.save(organizationMapper.dtoToEntity(organizationDTO));

        Rule rule = ruleMapper.dtoToEntity(organizationDTO.getRules());
        rule.setOrganizationId(organization.getId());
        ruleService.validateRule(rule);
        ruleRepository.save(rule);

        userService.createOwner(user, organization.getId());

        return organizationMapper.entityToResponseDto(organization);
    }

    public Organization isOrganizationActive(UUID organizationId) {
        return findOrganizationById(organizationId);

//        TODO: refactor to check active state of organization
//        Organization organization = findOrganizationById(organizationId);
//        if (organization.getActiveEndDate() == null || organization.getActiveEndDate().isBefore(LocalDateTime.now())) {
//            throw new BadRequestException("Organization with id: " + organizationId + " doesn't active.");
//        }
//        return organization;
    }

    public Organization findOrganizationById(UUID id) {
        return Optional.of(organizationRepository.findOrganizationById(id))
                .orElseThrow(() -> new NotFoundException("Organization with id: " + id + " doesn't exist."));
    }
}
