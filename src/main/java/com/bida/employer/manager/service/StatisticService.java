package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.StatisticByShiftDTOResponse;
import com.bida.employer.manager.repository.ShiftRepositoryCustom;
import com.bida.employer.manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Service
public class StatisticService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShiftRepositoryCustom shiftRepositoryCustom;

    public List<StatisticByShiftDTOResponse> getStatisticByShift(LocalDate from, LocalDate to) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<User> users = userRepository.findAllByOrganizationId(currentUser.getOrganizationId());
        List<Shift> shifts = shiftRepositoryCustom.findByFilters(null, currentUser.getOrganizationId(),
                false, from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        LocalDateTime now = LocalDateTime.now();

        List<StatisticByShiftDTOResponse> statisticByShifts = new LinkedList<>();
        for (User user : users) {
            StatisticByShiftDTOResponse record = new StatisticByShiftDTOResponse();
            record.setUserId(user.getId());
            record.setFirstName(user.getFirstName());
            record.setLastName(user.getLastName());
            record.setActiveUser(user.isActive());
            int shiftsCount = 0;
            int futureShiftsCount = 0;
            int finishedShiftsCount = 0;
            int shiftsDuration = 0;
            int futureShiftsDuration = 0;
            int finishedShiftsDuration = 0;
            for (Shift shift : shifts) {
                if (shift.getUserId().equals(user.getId())) {
                    shiftsCount++;
                    shiftsDuration += ChronoUnit.HOURS.between(shift.getShiftStart(), shift.getShiftFinish());
                    if (shift.getShiftFinish().isBefore(now)) {
                        finishedShiftsCount++;
                        finishedShiftsDuration += ChronoUnit.HOURS.between(shift.getShiftStart(), shift.getShiftFinish());
                    } else {
                        futureShiftsCount++;
                        futureShiftsDuration += ChronoUnit.HOURS.between(shift.getShiftStart(), shift.getShiftFinish());
                    }
                }
            }
            record.setShiftCount(shiftsCount);
            record.setFutureShiftCount(futureShiftsCount);
            record.setFinishedShiftCount(finishedShiftsCount);
            record.setShiftDuration(shiftsDuration);
            record.setFutureShiftDuration(futureShiftsDuration);
            record.setFinishedShiftDuration(finishedShiftsDuration);
            statisticByShifts.add(record);
        }
        return statisticByShifts;
    }
}
