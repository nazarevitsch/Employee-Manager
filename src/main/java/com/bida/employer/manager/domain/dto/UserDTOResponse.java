package com.bida.employer.manager.domain.dto;

import com.bida.employer.manager.domain.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDTOResponse {

    private UUID id;
    private String email;
    private String phoneNumber;
    private UUID organizationId;
    private LocalDateTime creationDate;
    private boolean isActive;
    private boolean isDeleted;
    private UserRole userRole;
}
