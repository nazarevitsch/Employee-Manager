package com.bida.employer.manager.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ShiftDTOResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftFinish;
    private LocalDateTime lastModificationDate;
    private UUID lastModificationUser;
    private UUID userId;
    private UserDTOResponse user;

    private List<TaskDTOResponse> tasks;
    private List<CheckInOutDTOResponse> checks;
}
