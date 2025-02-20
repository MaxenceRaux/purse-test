package com.maxenceraux.purse_test.controller;

import com.maxenceraux.purse_test.exception.MissingPurchaseException;
import com.maxenceraux.purse_test.exception.UnsupportedMethodChangeException;
import com.maxenceraux.purse_test.exception.UnsupportedStatusChangeException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@Hidden
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnsupportedStatusChangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleUserNotFound(UnsupportedStatusChangeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }

    @ExceptionHandler(MissingPurchaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ResponseEntity<String>> handleMissingPayment(MissingPurchaseException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedMethodChangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleUnsupportedPaymentChange(UnsupportedMethodChangeException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }

}