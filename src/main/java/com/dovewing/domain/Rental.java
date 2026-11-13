package com.dovewing.domain;

import java.util.Objects;

public final class Rental {
    private final int id;
    private final Vehicle vehicle;
    private final Renter renter;
    private final int days;
    private final double cost;
    private final double deposit;
    private RentalStatus status;

    Rental(int id, Vehicle vehicle, Renter renter, int days, double cost, double deposit) {
        if (id <= 0) {
            throw new IllegalArgumentException("Rental id must be positive: " + id);
        }
        Objects.requireNonNull(vehicle, "Vehicle is required");
        Objects.requireNonNull(renter, "Renter is required");
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be at least 1: " + days);
        }
        if (!(cost >= 0)) {
            throw new IllegalArgumentException("Rental cost must not be negative: " + cost);
        }
        if (!(deposit >= 0)) {
            throw new IllegalArgumentException("Deposit must not be negative: " + deposit);
        }

        this.id = id;
        this.vehicle = vehicle;
        this.renter = renter;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = RentalStatus.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Renter getRenter() {
        return renter;
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
