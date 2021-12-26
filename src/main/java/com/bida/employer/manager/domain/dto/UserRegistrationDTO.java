package com.bida.employer.manager.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRegistrationDTO {

    @NotNull(message = "Email can't be null.")
    private String email;

    @NotNull(message = "Phone number can't be null.")
    private String phoneNumber;

    @NotNull(message = "Password can't be null.")
    private String password;

    @NotNull(message = "First name can't be null.")
    private String firstName;

    @NotNull(message = "Last name can't be null.")
    private String lastName;
}
