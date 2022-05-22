package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.*;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.ShiftDTOResponse;
import com.bida.employer.manager.domain.dto.ShiftTimeDTO;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.ShiftMapper;
import com.bida.employer.manager.repository.ShiftRepository;
import com.bida.employer.manager.repository.ShiftRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private ShiftRepositoryCustom shiftRepositoryCustom;
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftMapper shiftMapper;
    @Autowired
    private RuleService ruleService;


    public void create(CreateShiftDTO createShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<Shift> shifts = new LinkedList<>();

        if (createShiftDTO.getUserIds() == null || createShiftDTO.getShifts().length == 0) {
            Rule rule = ruleService.findRuleByOrganizationId(currentUser.getOrganizationId());
            if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.PROHIBITED)) {
                throw new BadRequestException("You can't create unassigned shifts!");
            }
            shifts.addAll(createShifts(currentUser.getId(), currentUser.getOrganizationId(), null, createShiftDTO));
        } else {
            for (UUID userId : createShiftDTO.getUserIds()) {
                User user = userService.findUserById(userId);
                if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
                    throw new BadRequestException("User with id: " + user.getId() + " is from another organization!");
                }
                shifts.addAll(createShifts(currentUser.getId(), currentUser.getOrganizationId(), userId, createShiftDTO));
            }
        }
        shiftRepository.saveAll(shifts);
    }

    private List<Shift> createShifts(UUID currentUserId, UUID organizationId, UUID userId, CreateShiftDTO createShiftDTO) {
        List<Shift> handledShifts = new LinkedList<>();

        for (ShiftTimeDTO shiftTimeDTO : createShiftDTO.getShifts()) {
            if (shiftTimeDTO.getShiftStart().isAfter(shiftTimeDTO.getShiftFinish())) {
                throw new BadRequestException("Start of the shift after finish of the shift!");
            }
            handledShifts.add(shiftMapper.dtoToEntity(currentUserId, organizationId, userId, createShiftDTO, shiftTimeDTO));
        }
        return handledShifts;
    }

    public ShiftDTOResponse update(UpdateShiftDTO updateShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Shift existedShift = findById(updateShiftDTO.getShiftId());
        User user = userService.findUserById(existedShift.getUserId());

        if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("Shift with id: " + updateShiftDTO.getShiftId() + " belongs to user from another organization!");
        }
        Shift newShift = shiftMapper.dtoToEntity(updateShiftDTO);
        newShift.setLastModificationUser(currentUser.getId());
        newShift.setUserId(user.getId());
        return shiftMapper.entityToDto(shiftRepository.save(newShift));
    }

    public List<ShiftDTOResponse> getShifts(UUID userId, boolean unassignedShifts, LocalDate from, LocalDate to) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Shift> shifts = shiftRepositoryCustom.findByFilters(userId, currentUser.getOrganizationId(), unassignedShifts,
                from.atStartOfDay(), to.atStartOfDay());

        return shiftMapper.entityToDto(shifts);
    }

    public ShiftDTOResponse getShift(UUID shiftId) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Shift shift = findById(shiftId);
        if (!currentUser.getOrganizationId().equals(shift.getUser().getOrganizationId())) {
            throw new BadRequestException("Shift with id: " + shiftId + " belongs to user from another organization!");
        }
        return shiftMapper.entityToDto(shift);
    }

    public void delete(List<UUID> shiftIds) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        shiftRepository.deleteShiftsByIdsAndOrganizationId(currentUser.getOrganizationId(), shiftIds);
    }

    public Shift findById(UUID shiftId) {
       return shiftRepository.findById(shiftId)
               .orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " doesn't exist!"));
    }
}
