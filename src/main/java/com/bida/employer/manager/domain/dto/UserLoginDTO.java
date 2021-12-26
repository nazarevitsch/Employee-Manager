package com.bida.employer.manager.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserLoginDTO {

    @NotNull(message = "Email can't be null")
    private String email;

    @NotNull(message = "Password can't be null")
    private String password;
}
