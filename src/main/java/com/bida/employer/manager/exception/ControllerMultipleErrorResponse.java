package com.bida.employer.manager.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ControllerMultipleErrorResponse {
    private String errorCode;
    private List<String> message;
}
