package com.dovewing.app;

import com.dovewing.domain.Depot;
import com.dovewing.domain.Renter;
import com.dovewing.domain.Vehicle;

public class RentalTrackerApp {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", 150.00, 500.00, "ECONOMY");
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", 600.00, 2000.00, "PREMIUM");
        d.addVehicle(vitz);
        d.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", "CASUAL");
        Renter ama = new Renter("Ama Owusu", "MEMBER");
        d.addRenter(kofi);
        d.addRenter(ama);

        // a normal day
        d.rent(kofi, vitz, 3);
        d.rent(ama, prado, 2);
        d.returnVehicle(prado);

        // ... and the sort of thing that makes them keep a paper backup:
        d.rent(ama, vitz, 2); // the Vitz is already out with Kofi, but nothing stops this

        d.printReport();
    }
}