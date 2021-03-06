package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.ShiftWithAppliesDTOResponse;
import com.bida.employer.manager.domain.dto.CheckInOutDTO;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.ShiftDTOResponse;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import com.bida.employer.manager.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping("/next")
    public ResponseEntity<ShiftDTOResponse> getNextShift() {
        return new ResponseEntity<>(shiftService.getNextShift(), HttpStatus.OK);
    }

    @PostMapping("/{shiftId}/unassigned")
    public ResponseEntity<?> applyUnassignedShift(@PathVariable("shiftId") UUID shiftId) {
        shiftService.applyUnassignedShift(shiftId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{shiftId}/unassigned")
    public ResponseEntity<?> deleteApplyingUnassignedShift(@PathVariable("shiftId") UUID shiftId) {
        shiftService.deleteApplyingUnassignedShift(shiftId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<List<ShiftWithAppliesDTOResponse>> getApplyingUnassignedShiftS() {
        return new ResponseEntity<>(shiftService.getUnassignedShiftsWithAppliedUsers(), HttpStatus.OK);
    }

    @PostMapping("checkInOut")
    public ResponseEntity<?> checkInOut(@RequestBody @Valid CheckInOutDTO checkInOutDTO){
        shiftService.checkInOut(checkInOutDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{shiftId}")
    public ResponseEntity<ShiftDTOResponse> getShift(@PathVariable ("shiftId") UUID shiftId) {
        return new ResponseEntity<>(shiftService.getShift(shiftId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ShiftDTOResponse>> getShifts(@RequestParam(value = "userId", required = false) UUID userId,
                                                            @RequestParam(value = "unassigned", required = false) boolean unassignedShifts,
                                                            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return new ResponseEntity<>(shiftService.getShifts(userId, unassignedShifts, from, to), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<List<UUID>> create(@RequestBody @Valid CreateShiftDTO createShiftDTO) {
        return new ResponseEntity<>(shiftService.create(createShiftDTO), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<ShiftDTOResponse> update(@RequestBody @Valid UpdateShiftDTO updateShiftDTO) {
        return new ResponseEntity<>(shiftService.update(updateShiftDTO), HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<?> delete(@RequestParam("shiftIds") List<UUID> shiftIds) {
        shiftService.delete(shiftIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
