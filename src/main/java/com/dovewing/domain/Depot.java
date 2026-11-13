package com.dovewing.domain;

import java.util.ArrayList;
import java.util.List;

public class Depot {
    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();

    public Depot(String name) {
        this.name = name;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addRenter(Renter renter) {
        renters.add(renter);
    }

    // Rent a vehicle to a renter for a number of days. The rules depend on the class of vehicle.
    public void rent(Renter renter, Vehicle vehicle, int days) {
        double cost = 0, deposit = 0;
        if (vehicle.getCategory().equals("ECONOMY")) {
            cost = vehicle.getDailyRate() * days;
            deposit = vehicle.getDeposit();
            if (renter.getKind() == RenterType.MEMBER) {
                deposit = 0; // members have their deposit waived
            }
            vehicle.markRented();
            System.out.println("Rented " + vehicle.getModel() + " (" + vehicle.getReg() + ") to " + renter.getName()
                    + " for " + days + " days, cost GHS " + cost + ", deposit GHS " + deposit);
        } else if (vehicle.getCategory().equals("PREMIUM")) {
            cost = vehicle.getDailyRate() * days;
            deposit = vehicle.getDeposit();
            if (renter.getKind() == RenterType.MEMBER) {
                deposit = 0; // members have their deposit waived
            }
            vehicle.markRented();
            System.out.println("Rented " + vehicle.getModel() + " (" + vehicle.getReg() + ") to " + renter.getName()
                    + " for " + days + " days, cost GHS " + cost + ", deposit GHS " + deposit);
        }
        renter.addHistory("Rented " + vehicle.getModel());
        rentals.add(new Rental(renter.getName(), vehicle.getReg(), days, cost, deposit));
    }

    public void returnVehicle(Vehicle vehicle) {
        vehicle.markReturned();
        for (Rental rental : rentals) {
            if (rental.getVehicleReg().equals(vehicle.getReg()) && rental.getStatus() == RentalStatus.ACTIVE) {
                rental.markReturned();
            }
        }
        System.out.println("Returned " + vehicle.getModel() + " (" + vehicle.getReg() + ")");
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Fleet status:");
        for (Vehicle vehicle : vehicles) {
            System.out.println("  " + vehicle.getModel() + " (" + vehicle.getReg() + ", " + vehicle.getCategory()
                    + "): " + vehicle.getStatus());
        }
        System.out.println("Active rentals:");
        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.ACTIVE) {
                System.out.println("  Rental #" + rental.getId() + ": " + rental.getVehicleReg() + " -> "
                        + rental.getRenterName() + " (" + rental.getDays() + " days)");
            }
        }
    }
}

