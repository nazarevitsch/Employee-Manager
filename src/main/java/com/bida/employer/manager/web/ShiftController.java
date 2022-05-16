package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import com.bida.employer.manager.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<?> create(@RequestBody @Valid CreateShiftDTO createShiftDTO) {
        shiftService.create(createShiftDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<?> update(@RequestBody @Valid UpdateShiftDTO updateShiftDTO) {
        shiftService.update(updateShiftDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<?> delete(@RequestParam("shiftIds") List<UUID> shiftIds) {
        shiftService.delete(shiftIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
