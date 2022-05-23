package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateShiftDTO {

    @NotNull
    private UUID shiftId;

    private UUID userId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime shiftStart;

    @NotNull
    private LocalDateTime shiftFinish;
}
