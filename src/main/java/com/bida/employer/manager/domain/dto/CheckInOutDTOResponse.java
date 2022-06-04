package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.CheckInOutEnum;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CheckInOutDTOResponse {

    private UUID id;
    private LocalDateTime checkTime;
    private String note;
    private CheckInOutEnum checkInOut;
    private UUID shiftId;
}
