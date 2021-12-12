package com.bida.employer.manager.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ActivationDTO {

    private UUID id;
    private String activationCode;
    private String password;
}
