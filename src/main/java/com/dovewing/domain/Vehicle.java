package com.dovewing.domain;

public class Vehicle {
    private final String model;
    private final String reg;
    private final double dailyRate;
    private final double deposit;
    private final String category; // "ECONOMY" or "PREMIUM"
    private VehicleStatus status;

    public Vehicle(String model, String reg, double dailyRate, double deposit, String category) {
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
