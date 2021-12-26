package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.OrganizationCreateDTO;
import com.bida.employer.manager.domain.dto.OrganizationDTOResponse;
import com.bida.employer.manager.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/create")
    public ResponseEntity<OrganizationDTOResponse> create(@Valid @RequestBody OrganizationCreateDTO organizationDTO) {
        return new ResponseEntity<>(organizationService.create(organizationDTO), HttpStatus.CREATED);
    }
}