package com.bida.employer.manager.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException {

    private final String message;

    public String getErrorCode() {
        return "bad_request";
    }
}
