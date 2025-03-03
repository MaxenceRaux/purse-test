package com.maxenceraux.purse_test.exception;

import com.maxenceraux.purse_test.model.PaymentStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnsupportedStatusChangeException extends RuntimeException {

    private final PaymentStatus baseStatus;
    private final PaymentStatus intendedStatus;

    @Override
    public String getMessage() {
        return "Cannot change status from " + baseStatus + " to " + intendedStatus;
    }
}
