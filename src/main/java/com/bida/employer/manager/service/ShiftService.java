package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.ShiftTimeDTO;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.ShiftMapper;
import com.bida.employer.manager.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftMapper shiftMapper;


    public void create(CreateShiftDTO createShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Shift> shifts = new LinkedList<>();

        for (UUID userId : createShiftDTO.getUserIds()) {
            User user = userService.findUserById(userId);
            if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
                throw new BadRequestException("User with id: " + user.getId() + " is from another organization!");
            }

            List<Shift> handledShifts = new LinkedList<>();
            for (ShiftTimeDTO shiftTimeDTO : createShiftDTO.getShifts()) {
                if (shiftTimeDTO.getShiftStart().isAfter(shiftTimeDTO.getShiftFinish())) {
                    throw new BadRequestException("Start of the shift after finish of the shift.");
                }
                Shift shift = new Shift();
                shift.setTitle(createShiftDTO.getTitle());
                shift.setDescription(createShiftDTO.getDescription());
                shift.setLastModificationUser(currentUser.getOrganizationId());
                shift.setLastModificationDate(LocalDateTime.now());
                shift.setShiftStart(shiftTimeDTO.getShiftStart());
                shift.setShiftFinish(shiftTimeDTO.getShiftFinish());
                handledShifts.add(shift);
            }

            shifts.addAll(handledShifts);
        }

        shiftRepository.saveAll(shifts);
    }

    public void update(UpdateShiftDTO updateShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Shift existedShift = findById(updateShiftDTO.getShiftId());
        User user = userService.findUserById(existedShift.getUserId());

        if (user.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("Shift with id: " + updateShiftDTO.getShiftId() + " belongs to user from another organization!");
        }
        Shift newShift = shiftMapper.dtoToEntity(updateShiftDTO);
        newShift.setLastModificationUser(currentUser.getId());
        newShift.setUserId(user.getId());
        shiftRepository.save(newShift);
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
