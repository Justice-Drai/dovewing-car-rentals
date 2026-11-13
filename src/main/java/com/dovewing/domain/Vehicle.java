package com.dovewing.domain;

public abstract class Vehicle {
    private final String model;
    private final String reg;
    private final double dailyRate;
    private final double baseDeposit;
    private VehicleStatus status;

    protected Vehicle(String model, String reg, double dailyRate, double baseDeposit) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model must not be blank: " + model);
        }
        if (reg == null || reg.isBlank()) {
            throw new IllegalArgumentException("Registration must not be blank: " + reg);
        }
        if (!(dailyRate > 0)) {
            throw new IllegalArgumentException("Daily rate must be positive: " + dailyRate);
        }
        if (!(baseDeposit >= 0)) {
            throw new IllegalArgumentException("Deposit must not be negative: " + baseDeposit);
        }

        this.model = model;
        this.reg = reg;
        this.dailyRate = dailyRate;
        this.baseDeposit = baseDeposit;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getModel() {
        return model;
    }

    public String getReg() {
        return reg;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public double getBaseDeposit() {
        return baseDeposit;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public final double costFor(int days) {
        return dailyRate * days;
    }

    protected final double standardDeposit(Renter renter) {
        return renter.getKind().waivesDeposit() ? 0 : baseDeposit;
    }

    public abstract double depositFor(Renter renter);

    public abstract String getCategory();

    void markRented() {
        status = VehicleStatus.RENTED;
    }

    void markReturned() {
        status = VehicleStatus.AVAILABLE;
    }
}
