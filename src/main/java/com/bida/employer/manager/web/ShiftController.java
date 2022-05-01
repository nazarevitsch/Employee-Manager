package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<?> create(@RequestBody @Valid CreateShiftDTO createShiftDTO) {
        shiftService.create(createShiftDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
