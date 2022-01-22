package com.bida.employer.manager.domain.dto;

import lombok.Data;

@Data
public class CheckEmailDTOResponse {

    private String email;
    private boolean isActive;
}
