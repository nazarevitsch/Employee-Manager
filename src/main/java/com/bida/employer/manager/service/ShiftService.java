package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.EmployeeDTO;
import com.bida.employer.manager.domain.dto.ShiftDTO;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserService userService;


    public void create(CreateShiftDTO createShiftDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<Shift> shifts = new LinkedList<>();

        for (EmployeeDTO employeeDTO : createShiftDTO.getEmployees()) {
            User user = userService.findUserById(employeeDTO.getUserId());
            if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
                throw new BadRequestException("User with id: " + user.getId() + " is from another organization!");
            }

            List<Shift> handledShifts = new LinkedList<>();
            for (ShiftDTO shiftDTO : employeeDTO.getShifts()) {
                Shift shift = new Shift();
                shift.setTitle(employeeDTO.getTitle());
                shift.setDescription(employeeDTO.getDescription());
                shift.setLastModificationUser(currentUser.getOrganizationId());
                shift.setLastModificationDate(LocalDateTime.now());
                shift.setShiftStart(shiftDTO.getShiftStart());
                shift.setShiftFinish(shiftDTO.getShiftFinish());
                handledShifts.add(shift);
            }

            shifts.addAll(handledShifts);
        }

        shiftRepository.saveAll(shifts);
    }
}
