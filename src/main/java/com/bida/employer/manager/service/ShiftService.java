package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.*;
import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.domain.enums.CheckInOutEnum;
import com.bida.employer.manager.domain.enums.NotAssignedShiftRule;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.CheckInOutMapper;
import com.bida.employer.manager.mapper.ShiftMapper;
import com.bida.employer.manager.repository.ApplyUnassignedShiftRepository;
import com.bida.employer.manager.repository.CheckInOutRepository;
import com.bida.employer.manager.repository.ShiftRepository;
import com.bida.employer.manager.repository.ShiftRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private CheckInOutRepository checkInOutRepository;
    @Autowired
    private ShiftRepositoryCustom shiftRepositoryCustom;
    @Autowired
    private ApplyUnassignedShiftRepository applyUnassignedShiftRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftMapper shiftMapper;
    @Autowired
    private CheckInOutMapper checkInOutMapper;
    @Autowired
    private RuleService ruleService;

    public void applyUnassignedShift(UUID shiftId){
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Shift shift = findById(shiftId);
        if (!shift.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("You can't apply unassigned shift of user from another organization!");
        }
        if (!shift.getShiftStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("You can't apply old unassigned shift!");
        }
        if (shift.getUserId() != null) {
            throw new BadRequestException("You can't apply assigned shift!");
        }

        ApplyUnassignedShift applyUnassignedShift = new ApplyUnassignedShift();
        applyUnassignedShift.setUserId(currentUser.getId());
        applyUnassignedShift.setShiftId(shift.getId());
        applyUnassignedShiftRepository.save(applyUnassignedShift);
    }

    public void checkInOut(CheckInOutDTO checkInOutDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        CheckInOut checkInOut = checkInOutMapper.dtoToEntity(checkInOutDTO);
        Shift shift = findById(checkInOut.getShiftId());
        if (!shift.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("You can't check in/out shift of user from another organization!");
        }

        List<CheckInOut> checkInOuts = checkInOutRepository.findAllByShiftId(checkInOut.getShiftId());
        CheckInOut checkIn = findCheckIn(checkInOuts, CheckInOutEnum.CHECK_IN);
        CheckInOut checkOut = findCheckIn(checkInOuts, CheckInOutEnum.CHECK_OUT);

        if (checkIn != null && checkOut != null) {
            throw new BadRequestException("Shift with id: " + shift.getId() + " is finished!");
        }
        if (checkIn != null && checkInOut.getCheckInOut().equals(CheckInOutEnum.CHECK_IN)) {
            throw new BadRequestException("Shift with id: " + shift.getId() + " was already checked in!");
        }
        if (checkIn == null && checkInOut.getCheckInOut().equals(CheckInOutEnum.CHECK_OUT)) {
            throw new BadRequestException("Shift with id: " + shift.getId() + " wasn't checked in!");
        }
        if (checkInOut.getCheckInOut().equals(CheckInOutEnum.CHECK_IN) && shift.getShiftStart().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Too early!");
        }
        if (checkOut != null && checkInOut.getCheckInOut().equals(CheckInOutEnum.CHECK_OUT)) {
            throw new BadRequestException("Shift with id: " + shift.getId() + " was already checked out!");
        }
        if (checkInOut.getCheckInOut().equals(CheckInOutEnum.CHECK_OUT) && shift.getShiftFinish().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Too early!");
        }
        checkInOutRepository.save(checkInOut);
    }

    private CheckInOut findCheckIn(List<CheckInOut> checkInOuts, CheckInOutEnum checkInOutEnum) {
        for (CheckInOut checkInOut : checkInOuts) {
            if (checkInOut.getCheckInOut().equals(checkInOutEnum)) return checkInOut;
        }
        return null;
    }


    public List<UUID> create(CreateShiftDTO createShiftDTO) {
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
        List<Shift> savedShifts = shiftRepository.saveAll(shifts);
        return savedShifts.stream().map(Shift::getId).collect(Collectors.toList());
    }

    private List<Shift> createShifts(UUID currentUserId, UUID organizationId, UUID userId, CreateShiftDTO createShiftDTO) {
        List<Shift> handledShifts = new LinkedList<>();

        for (ShiftTimeDTO shiftTimeDTO : createShiftDTO.getShifts()) {
            if (shiftTimeDTO.getShiftStart().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Shift can't start before now!");
            }
            if (shiftTimeDTO.getShiftStart().isAfter(shiftTimeDTO.getShiftFinish())) {
                throw new BadRequestException("Start of the shift after finish of the shift!");
            }
            handledShifts.add(shiftMapper.dtoToEntity(currentUserId, organizationId, userId, createShiftDTO, shiftTimeDTO));
        }
        return handledShifts;
    }

    public ShiftDTOResponse update(UpdateShiftDTO updateShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (updateShiftDTO.getShiftStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Shift can't start before now!");
        }
        if (updateShiftDTO.getShiftStart().isAfter(updateShiftDTO.getShiftFinish())) {
            throw new BadRequestException("Start of the shift after finish of the shift!");
        }
        Shift existedShift = findById(updateShiftDTO.getShiftId());

        User user = null;
        if (updateShiftDTO.getUserId() == null) {
            Rule rule = ruleService.findRuleByOrganizationId(currentUser.getOrganizationId());
            if (rule.getNotAssignedShiftRule().equals(NotAssignedShiftRule.PROHIBITED)) {
                throw new BadRequestException("You can't update shift to unassigned!");
            }
        } else {
            user = userService.findUserById(updateShiftDTO.getUserId());
            if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
                throw new BadRequestException("Shift with id: " + updateShiftDTO.getShiftId() + " belongs to user from another organization!");
            }
        }

        existedShift.setLastModificationDate(LocalDateTime.now());
        existedShift.setUserId(updateShiftDTO.getUserId());
        existedShift.setTitle(updateShiftDTO.getTitle());
        existedShift.setDescription(updateShiftDTO.getDescription());
        existedShift.setShiftStart(updateShiftDTO.getShiftStart());
        existedShift.setShiftFinish(updateShiftDTO.getShiftFinish());

        return shiftMapper.entityToDto(shiftRepository.save(existedShift));
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

        List<Shift> shifts = shiftRepository.findAllById(shiftIds);
        for (Shift shift : shifts) {
            if (shift.getShiftStart().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("You can't remove old shift!");
            }
            if (!shift.getOrganizationId().equals(currentUser.getOrganizationId())) {
                throw new BadRequestException("You can't remove shift of users from another organization!");
            }
        }
        shiftRepository.deleteAllById(shiftIds);
    }

    public Shift findById(UUID shiftId) {
       return shiftRepository.findById(shiftId)
               .orElseThrow(() -> new NotFoundException("Shift with id: " + shiftId + " doesn't exist!"));
    }
}
