package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Organization;
import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.OrganizationMapper;
import com.bida.employer.manager.mapper.RuleMapper;
import com.bida.employer.manager.repository.OrganizationRepository;
import com.bida.employer.manager.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public OrganizationDTOResponse getOrganizationOfCurrentUser() {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        OrganizationDTOResponse organization = organizationMapper.entityToResponseDto(findOrganizationById(currentUser.getOrganizationId()));
        organization.setRules(ruleMapper.entityToDto(ruleService.findRuleByOrganizationId(currentUser.getOrganizationId())));
        return organization;
    }

    public OrganizationDTOResponse create(OrganizationCreateDTO organizationDTO) {
        UserRegistrationDTO user = organizationDTO.getUser();
        userService.ownerCreationValidation(user);

        Organization organization = organizationRepository.save(organizationMapper.dtoToEntity(organizationDTO));

        Rule rule = ruleMapper.dtoToEntity(organizationDTO.getRules());
        rule.setOrganizationId(organization.getId());
        ruleService.validateRule(rule);
        Rule savedRule = ruleRepository.save(rule);

        userService.createOwner(user, organization.getId());
        OrganizationDTOResponse organizationDTOResponse = organizationMapper.entityToResponseDto(organization);
        organizationDTOResponse.setRules(ruleMapper.entityToDto(savedRule));

        return organizationDTOResponse;
    }

    public OrganizationDTOResponse updateRules(RuleDTO ruleDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Organization currentOrganization = findOrganizationById(currentUser.getOrganizationId());

        Rule newRule = ruleMapper.dtoToEntity(ruleDTO);
        ruleService.validateRule(newRule);
        Rule existedRule = ruleService.findRuleByOrganizationId(currentOrganization.getId());

        newRule.setId(existedRule.getId());
        newRule.setOrganizationId(existedRule.getOrganizationId());
        newRule.setLastUpdateDate(LocalDateTime.now());

        Rule savedRule = ruleRepository.save(newRule);

        OrganizationDTOResponse organizationDTOResponse = organizationMapper.entityToResponseDto(currentOrganization);
        organizationDTOResponse.setRules(ruleMapper.entityToDto(savedRule));
        return organizationDTOResponse;
    }

    public OrganizationDTOResponse patchOrganizationSize(OrganizationSizeDTO organizationSizeDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Organization currentOrganization = findOrganizationById(currentUser.getOrganizationId());

        if (currentOrganization.getOrganizationType() == organizationSizeDTO.getOrganizationType()) {
            throw new BadRequestException("Organization has already such size!");
        }
        currentOrganization.setOrganizationType(organizationSizeDTO.getOrganizationType());
        Organization savedOrganization = organizationRepository.save(currentOrganization);

        OrganizationDTOResponse organizationDTOResponse = organizationMapper.entityToResponseDto(savedOrganization);
        organizationDTOResponse.setRules(ruleMapper.entityToDto(ruleRepository.findRuleByOrganizationId(currentOrganization.getId())));

        return organizationDTOResponse;
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
