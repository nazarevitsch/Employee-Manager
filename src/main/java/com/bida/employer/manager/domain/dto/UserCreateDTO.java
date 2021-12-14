package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.UserRole;
import lombok.Data;

@Data
public class UserCreateDTO {

    private String email;
    private String phoneNumber;
    private UserRole userRole;
}
