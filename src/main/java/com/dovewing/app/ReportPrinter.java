package com.dovewing.app;

import com.dovewing.domain.Depot;
import com.dovewing.domain.Renter;
import com.dovewing.domain.Rental;
import com.dovewing.domain.Vehicle;

import java.io.PrintStream;
import java.util.Objects;

public class ReportPrinter {

    private static final String CURRENCY = "GHS";

    private final PrintStream out;

    public ReportPrinter(PrintStream out) {
        this.out = Objects.requireNonNull(out, "Output stream is required");
    }

    public void printRented(Rental rental) {
        Vehicle vehicle = rental.getVehicle();
        Renter renter = rental.getRenter();
        out.println("Rented " + vehicle.getModel() + " (" + vehicle.getReg() + ") to " + renter.getName()
                + " for " + rental.getDays() + " days, cost " + CURRENCY + " " + rental.getCost() + ", deposit "
                + CURRENCY + " " + rental.getDeposit());
    }

    public void printReturned(Vehicle vehicle) {
        out.println("Returned " + vehicle.getModel() + " (" + vehicle.getReg() + ")");
    }

    public void printRefused(String reason) {
        out.println("Refused: " + reason);
    }

    public void printEndOfDay(Depot depot) {
        out.println();
        out.println("=== " + depot.getName() + " - end of day ===");
        out.println("Fleet status:");
        for (Vehicle vehicle : depot.getVehicles()) {
            out.println("  " + vehicle.getModel() + " (" + vehicle.getReg() + ", " + vehicle.getCategory()
                    + "): " + vehicle.getStatus());
        }
        out.println("Active rentals:");
        for (Rental rental : depot.getActiveRentals()) {
            out.println("  Rental #" + rental.getId() + ": " + rental.getVehicle().getReg() + " -> "
                    + rental.getRenter().getName() + " (" + rental.getDays() + " days)");
        }
        out.println("Deposits held: " + CURRENCY + " " + depot.getDepositsHeld());
    }
}
