package com.maxenceraux.purse_test.model;

public enum PaymentStatus {
    IN_PROGRESS,
    AUTHORIZED,
    CAPTURED;

    public PaymentStatus next() {
        return switch (this) {
            case IN_PROGRESS -> AUTHORIZED;
            case AUTHORIZED -> CAPTURED;
            default -> null;
        };
    }
}
