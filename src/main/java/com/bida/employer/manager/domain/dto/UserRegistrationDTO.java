package com.bida.employer.manager.domain.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserRegistrationDTO {

    private String email;
    private String password;
    private String phoneNumber;
    private UUID organizationId;
}
