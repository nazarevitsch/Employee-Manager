package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.OrganizationCreateDTO;
import com.bida.employer.manager.domain.dto.OrganizationDTOResponse;
import com.bida.employer.manager.domain.dto.OrganizationSizeDTO;
import com.bida.employer.manager.domain.dto.RuleDTO;
import com.bida.employer.manager.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<OrganizationDTOResponse> getOrganization() {
        return new ResponseEntity<>(organizationService.getOrganizationOfCurrentUser(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrganizationDTOResponse> create(@Valid @RequestBody OrganizationCreateDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.create(organizationDTO), HttpStatus.CREATED);
    }

    @PutMapping("/rule")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<OrganizationDTOResponse> updateRules(@Valid @RequestBody RuleDTO ruleDTO) {
        return new ResponseEntity<>(organizationService.updateRules(ruleDTO), HttpStatus.OK);
    }

    @PatchMapping("/size")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<OrganizationDTOResponse> patchOrganizationSize(@Valid @RequestBody OrganizationSizeDTO organizationSizeDTO) {
        return new ResponseEntity<>(organizationService.patchOrganizationSize(organizationSizeDTO), HttpStatus.OK);
    }
}