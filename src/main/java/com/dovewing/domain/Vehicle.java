package com.dovewing.domain;

import java.util.Objects;

public class Vehicle {
    private final String model;
    private final String reg;
    private final double dailyRate;
    private final double deposit;
    private final String category; // "ECONOMY" or "PREMIUM"
    private VehicleStatus status;

    public Vehicle(String model, String reg, double dailyRate, double deposit, String category) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model must not be blank: " + model);
        }
        if (reg == null || reg.isBlank()) {
            throw new IllegalArgumentException("Registration must not be blank: " + reg);
        }
        if (!(dailyRate > 0)) {
            throw new IllegalArgumentException("Daily rate must be positive: " + dailyRate);
        }
        if (!(deposit >= 0)) {
            throw new IllegalArgumentException("Deposit must not be negative: " + deposit);
        }
        Objects.requireNonNull(category, "Vehicle category is required");

        this.model = model;
        this.reg = reg;
        this.dailyRate = dailyRate;
        this.deposit = deposit;
        this.category = category;
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

    public double getDeposit() {
        return deposit;
    }

    public String getCategory() {
        return category;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    void markRented() {
        status = VehicleStatus.RENTED;
    }

    void markReturned() {
        status = VehicleStatus.AVAILABLE;
    }
}
