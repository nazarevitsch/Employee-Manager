package com.bida.employer.manager.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDTOResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime taskTime;
    private UUID shiftId;
}
