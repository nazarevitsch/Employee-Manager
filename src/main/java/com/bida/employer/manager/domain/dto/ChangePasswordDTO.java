package com.bida.employer.manager.domain.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String oldPassword;
    private String newPassword;
}
