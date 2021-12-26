package com.bida.employer.manager.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<ControllerErrorResponse> handleBadRequestException(BadRequestException ex) {
        ControllerErrorResponse controllerResponse = ControllerErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();
        return new ResponseEntity<>(controllerResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<ControllerErrorResponse> handleNorFoundException(NotFoundException ex) {
        ControllerErrorResponse controllerResponse = ControllerErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();
        return new ResponseEntity<>(controllerResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ControllerMultipleErrorResponse controllerMultipleErrorResponse = ControllerMultipleErrorResponse.builder()
                .message(ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()))
                .errorCode("bad_request")
                .build();
        return new ResponseEntity<>(controllerMultipleErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
