package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PasswordRecoveryDTO {

    @NotNull(message = "Id can't be null!")
    private UUID userId;

    @NotNull(message = "Token code can't be null!")
    private String token;

    @NotNull(message = "Password can't be null!")
    private String password;
}
