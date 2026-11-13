package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;

public class Depot {
    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();

    public Depot(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Depot name must not be blank: " + name);
        }
        this.name = name;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addRenter(Renter renter) {
        renters.add(renter);
    }

    // Rent a vehicle to a renter for a number of days. The rules depend on the class of vehicle.
    public Rental rent(Renter renter, Vehicle vehicle, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be at least 1: " + days);
        }

        double cost = vehicle.costFor(days);
        double deposit = vehicle.depositFor(renter);
        vehicle.markRented();
        renter.addHistory("Rented " + vehicle.getModel());
        Rental rental = new Rental(renter.getName(), vehicle.getReg(), days, cost, deposit);
        rentals.add(rental);
        return rental;
    }

    public void returnVehicle(Vehicle vehicle) {
        vehicle.markReturned();
        for (Rental rental : rentals) {
            if (rental.getVehicleReg().equals(vehicle.getReg()) && rental.getStatus() == RentalStatus.ACTIVE) {
                rental.markReturned();
            }
        }
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public String getName() {
        return name;
    }

    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.ACTIVE) {
                activeRentals.add(rental);
            }
        }
        return activeRentals;
    }
}

