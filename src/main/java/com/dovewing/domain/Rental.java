package com.dovewing.domain;

import java.util.Objects;

public class Rental {
    private final int id;
    private final String renterName;
    private final String vehicleReg;
    private final int days;
    private final double cost;
    private final double deposit;
    private RentalStatus status;
    private static int counter = 1;

    public Rental(String renterName, String vehicleReg, int days, double cost, double deposit) {
        Objects.requireNonNull(renterName, "Renter name is required");
        Objects.requireNonNull(vehicleReg, "Vehicle registration is required");
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be at least 1: " + days);
        }
        if (!(cost >= 0)) {
            throw new IllegalArgumentException("Rental cost must not be negative: " + cost);
        }
        if (!(deposit >= 0)) {
            throw new IllegalArgumentException("Deposit must not be negative: " + deposit);
        }

        this.id = counter;
        counter++;
        this.renterName = renterName;
        this.vehicleReg = vehicleReg;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = RentalStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public String getRenterName() {
        return renterName;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public int getDays() {
        return days;
    }

    public double getCost() {
        return cost;
    }

    public double getDeposit() {
        return deposit;
    }

    public RentalStatus getStatus() {
        return status;
    }

    void markReturned() {
        status = RentalStatus.RETURNED;
    }
}
