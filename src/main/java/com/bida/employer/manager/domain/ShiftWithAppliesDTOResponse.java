package com.bida.employer.manager.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShiftWithAppliesDTOResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftFinish;
    private LocalDateTime lastModificationDate;
    private UUID lastModificationUser;
    private UUID userId;

//    private ;
}
