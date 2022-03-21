package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginDTO {

    @NotEmpty(message = "Email can't be null or empty!")
    private String email;

    @NotEmpty(message = "Password can't be null or empty!")
    private String password;
}
