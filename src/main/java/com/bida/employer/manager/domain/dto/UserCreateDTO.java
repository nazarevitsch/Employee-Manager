package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.UserRole;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateDTO {

    @NotNull(message = "Email can't be null.")
    private String email;

    @NotNull(message = "Phone number can't be null.")
    private String phoneNumber;

    @NotEmpty(message = "First name can't be null or empty.")
    private String firstName;

    @NotEmpty(message = "Last name can't be null or empty.")
    private String lastName;

    @NotNull(message = "User role can't be null.")
    private UserRole userRole;
}
