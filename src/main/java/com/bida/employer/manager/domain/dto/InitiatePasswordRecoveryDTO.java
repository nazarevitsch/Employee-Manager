package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InitiatePasswordRecoveryDTO {

    @NotNull(message = "Email should be not null!")
    private String email;
}
