package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ActivationDTO {

    @NotNull(message = "Id can't be null.")
    private UUID id;

    @NotNull(message = "Activation code can't be null.")
    private String activationCode;

    @NotNull(message = "Password can't be null.")
    private String password;
}
