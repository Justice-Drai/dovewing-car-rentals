package com.dovewing.domain;

public class Vehicle {
    public String model;
    public String reg;
    public double dailyRate;
    public double deposit;
    public String category; // "ECONOMY" or "PREMIUM"
    public String status;   // "AVAILABLE" or "RENTED"
    public int id;
    static int counter = 1;

    public Vehicle(String model, String reg, double dailyRate, double deposit, String category) {
        this.id = counter;
        counter++;
        this.model = model;
        this.reg = reg;
        this.dailyRate = dailyRate;
        this.deposit = deposit;
        this.category = category;
        this.status = "AVAILABLE";
    }
}
