package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskUpdateDTO {

    @NotNull(message = "Id can't be null!")
    private UUID id;

    @NotEmpty(message = "Title can't be empty!")
    private String title;

    @NotEmpty(message = "Description can't be empty!")
    private String description;

    private LocalDateTime taskTime;

    @NotNull(message = "Task time can't be null!")
    private UUID shiftId;
}
