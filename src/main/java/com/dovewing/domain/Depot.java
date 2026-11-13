package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Depot {
    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();
    private int nextRentalId = 1;

    public Depot(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Depot name must not be blank: " + name);
        }
        this.name = name;
    }

    public void addVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "Vehicle is required");
        for (Vehicle existing : vehicles) {
            if (existing.getReg().equalsIgnoreCase(vehicle.getReg())) {
                throw new IllegalArgumentException(
                        "A vehicle with registration " + vehicle.getReg() + " is already in the fleet");
            }
        }
        vehicles.add(vehicle);
    }

    public void addRenter(Renter renter) {
        Objects.requireNonNull(renter, "Renter is required");
        if (renters.contains(renter)) {
            throw new IllegalArgumentException(renter.getName() + " is already registered at this depot");
        }
        renters.add(renter);
    }

    // Rent a vehicle to a renter for a number of days. The rules depend on the class of vehicle.
    public Rental rent(Renter renter, Vehicle vehicle, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be at least 1: " + days);
        }
        if (!vehicles.contains(vehicle)) {
            throw new IllegalArgumentException(vehicle.getReg() + " is not in this depot's fleet");
        }
        if (!renters.contains(renter)) {
            throw new IllegalArgumentException(renter.getName() + " is not in this depot's renter list");
        }

        double cost = vehicle.costFor(days);
        double deposit = vehicle.depositFor(renter);
        vehicle.markRented();
        Rental rental = new Rental(nextRentalId++, vehicle, renter, days, cost, deposit);
        rentals.add(rental);
        return rental;
    }

    public Rental returnVehicle(Vehicle vehicle) {
        Rental activeRental = null;
        for (Rental rental : rentals) {
            if (rental.getVehicle() == vehicle && rental.getStatus() == RentalStatus.ACTIVE) {
                activeRental = rental;
                break;
            }
        }
        if (activeRental == null) {
            throw new VehicleStateException(vehicle.getModel() + " (" + vehicle.getReg()
                    + ") is not currently rented");
        }

        vehicle.markReturned();
        activeRental.markReturned();
        return activeRental;
    }

    public List<Rental> getRentals() {
        return List.copyOf(rentals);
    }

    public List<Rental> rentalsFor(Renter renter) {
        List<Rental> renterRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getRenter() == renter) {
                renterRentals.add(rental);
            }
        }
        return renterRentals;
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

