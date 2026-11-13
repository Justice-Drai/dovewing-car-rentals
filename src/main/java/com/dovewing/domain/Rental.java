package com.dovewing.domain;

public class Rental {
    public int id;
    public String renterName;
    public String vehicleReg;
    public int days;
    public double cost;
    public double deposit;
    public String status; // "ACTIVE" or "RETURNED"
    static int counter = 1;

    public Rental(String renterName, String vehicleReg, int days, double cost, double deposit) {
        this.id = counter;
        counter++;
        this.renterName = renterName;
        this.vehicleReg = vehicleReg;
        this.days = days;
        this.cost = cost;
        this.deposit = deposit;
        this.status = "ACTIVE";
    }
}
