package com.bida.employer.manager.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StatisticByShiftDTOResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private boolean activeUser;
    private int shiftCount;
    private int finishedShiftCount;
    private int futureShiftCount;
    private int shiftDuration;
    private int finishedShiftDuration;
    private int futureShiftDuration;
}
