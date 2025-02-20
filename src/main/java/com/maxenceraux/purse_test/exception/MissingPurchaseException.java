package com.maxenceraux.purse_test.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MissingPurchaseException extends RuntimeException {

    private Long paymentId;

    @Override
    public String getMessage() {
        return "Cannot find payment with id " + paymentId;
    }
}
