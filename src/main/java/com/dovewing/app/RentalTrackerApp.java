package com.dovewing.app;

import com.dovewing.domain.Depot;
import com.dovewing.domain.EconomyVehicle;
import com.dovewing.domain.PremiumVehicle;
import com.dovewing.domain.Renter;
import com.dovewing.domain.RenterType;
import com.dovewing.domain.Rental;
import com.dovewing.domain.Vehicle;
import com.dovewing.domain.VehicleStateException;

public class RentalTrackerApp {

    public static void main(String[] args) {
        Depot depot = new Depot("Dovewing Car Rentals");
        ReportPrinter printer = new ReportPrinter(System.out);

        Vehicle vitz = new EconomyVehicle("Toyota Vitz", "GT-1234-20", 150.00, 500.00);
        Vehicle prado = new PremiumVehicle("Toyota Prado", "GR-5678-22", 600.00, 2000.00);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        // a normal day
        Rental kofiVitz = depot.rent(kofi, vitz, 3);
        printer.printRented(kofiVitz);
        Rental amaPrado = depot.rent(ama, prado, 2);
        printer.printRented(amaPrado);
        depot.returnVehicle(prado);
        printer.printReturned(prado);

        // The Vitz is already out with Kofi, so the depot refuses this rental.
        try {
            Rental amaVitz = depot.rent(ama, vitz, 2);
            printer.printRented(amaVitz);
        } catch (VehicleStateException e) {
            printer.printRefused(e.getMessage());
        }

        printer.printEndOfDay(depot);
    }
}
