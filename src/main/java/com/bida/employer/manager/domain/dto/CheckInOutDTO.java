package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.CheckInOutEnum;
import java.time.LocalDateTime;
import java.util.UUID;

public class CheckInOutDTO {

    private LocalDateTime checkTime;
    private String note;
    private CheckInOutEnum checkInOut;
    private UUID shiftId;
}
