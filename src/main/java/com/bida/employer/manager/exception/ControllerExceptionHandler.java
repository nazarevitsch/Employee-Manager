package com.bida.employer.manager.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
        ControllerErrorResponse controllerErrorResponse = ControllerErrorResponse.builder()
                .message(ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()).get(0))
                .errorCode("bad_request")
                .build();
        return new ResponseEntity<>(controllerErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
