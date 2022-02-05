package com.bida.employer.manager.domain.dto;

import lombok.Data;

@Data
public class CheckUserDTOResponse {

    private String email;
    private boolean isActive;
}
