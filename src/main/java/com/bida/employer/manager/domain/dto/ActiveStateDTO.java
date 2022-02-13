package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ActiveStateDTO {

    @NotNull(message = "Id should be not null.")
    private UUID userId;

    @NotNull(message = "Active should be not null.")
    private Boolean active;
}
