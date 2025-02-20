package com.maxenceraux.purse_test.exception;

public class UnsupportedMethodChangeException extends RuntimeException {
    public UnsupportedMethodChangeException() {
        super("Payment is too advanced to change payment method.");
    }
}
