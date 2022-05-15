package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ShiftTimeDTO {

    @NotNull
    private LocalDateTime shiftStart;

    @NotNull
    private LocalDateTime shiftFinish;
}
