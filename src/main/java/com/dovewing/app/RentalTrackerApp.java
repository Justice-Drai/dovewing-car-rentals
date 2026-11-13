package com.dovewing.app;

import com.dovewing.domain.Depot;
import com.dovewing.domain.Renter;
import com.dovewing.domain.Rental;
import com.dovewing.domain.Vehicle;
import com.dovewing.domain.RenterType;

public class RentalTrackerApp {

    public static void main(String[] args) {
        Depot depot = new Depot("Dovewing Car Rentals");
        ReportPrinter printer = new ReportPrinter(System.out);

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", 150.00, 500.00, "ECONOMY");
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", 600.00, 2000.00, "PREMIUM");
        depot.addVehicle(vitz);
        depot.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addRenter(kofi);
        depot.addRenter(ama);

        // a normal day
        Rental kofiVitz = depot.rent(kofi, vitz, 3);
        printer.printRented(vitz, kofiVitz);
        Rental amaPrado = depot.rent(ama, prado, 2);
        printer.printRented(prado, amaPrado);
        depot.returnVehicle(prado);
        printer.printReturned(prado);

        // ... and the sort of thing that makes them keep a paper backup:
        Rental amaVitz = depot.rent(ama, vitz, 2); // the Vitz is already out with Kofi, but nothing stops this
        printer.printRented(vitz, amaVitz);

        printer.printEndOfDay(depot);
    }
}