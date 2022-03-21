package com.bida.employer.manager.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordDTO {

    @NotNull(message = "Old password can't be null!")
    private String oldPassword;

    @NotNull(message = "New password can't be null!")
    private String newPassword;
}
