package com.dovewing.domain;

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
