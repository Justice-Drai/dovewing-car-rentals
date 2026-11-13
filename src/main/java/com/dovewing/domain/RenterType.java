package com.dovewing.domain;

public enum RenterType {
    CASUAL(false),
    MEMBER(true);

    private final boolean depositWaived;

    RenterType(boolean depositWaived) {
        this.depositWaived = depositWaived;
    }

    public boolean waivesDeposit() {
        return depositWaived;
    }
}