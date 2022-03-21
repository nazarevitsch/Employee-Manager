package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserRoleDTO {

    @NotNull(message = "Id should be not null!")
    private UUID userId;

    @NotNull(message = "User Role should be not null!")
    private UserRole userRole;
}
