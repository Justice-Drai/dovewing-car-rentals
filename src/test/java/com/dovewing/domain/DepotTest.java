package com.dovewing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepotTest {

    private Depot depot;
    private Vehicle vitz;
    private Vehicle prado;
    private Renter kofi;
    private Renter ama;

    @BeforeEach
    void setUp() {
        depot = new Depot("Dovewing Car Rentals");
        vitz = new EconomyVehicle("Toyota Vitz", "GT-1234-20", 150.00, 500.00);
        prado = new PremiumVehicle("Toyota Prado", "GR-5678-22", 600.00, 2000.00);
        kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);
        depot.addRenter(kofi);
        depot.addRenter(ama);
    }

    @Test
    void rentChargesDailyRateTimesDays() {
        Rental rental = depot.rent(kofi, vitz, 3);

        assertEquals(450.0, rental.getCost());
    }

    @Test
    void casualRenterPaysTheVehicleDeposit() {
        Rental rental = depot.rent(kofi, vitz, 3);

        assertEquals(500.0, rental.getDeposit());
    }

    @Test
    void memberDepositIsWaived() {
        Rental rental = depot.rent(ama, prado, 2);

        assertEquals(0.0, rental.getDeposit());
    }

    @Test
    void rentingAVehicleThatIsOutIsRefusedAndLeavesNoTrace() {
        depot.rent(kofi, vitz, 3);

        assertThrows(VehicleStateException.class, () -> depot.rent(ama, vitz, 2));

        assertEquals(VehicleStatus.RENTED, vitz.getStatus());
        assertEquals(1, depot.getActiveRentals().size());
        assertTrue(depot.rentalsFor(ama).isEmpty());
    }

    @Test
    void refusedRentalDoesNotUseARentalNumber() {
        depot.rent(kofi, vitz, 3);
        assertThrows(VehicleStateException.class, () -> depot.rent(ama, vitz, 2));

        Rental next = depot.rent(ama, prado, 2);

        assertEquals(2, next.getId());
    }

    @Test
    void returnClosesThatRentalAndFreesTheVehicle() {
        Rental rental = depot.rent(kofi, vitz, 3);

        Rental returned = depot.returnVehicle(vitz);

        assertSame(rental, returned);
        assertEquals(RentalStatus.RETURNED, returned.getStatus());
        assertEquals(VehicleStatus.AVAILABLE, vitz.getStatus());
        assertTrue(depot.getActiveRentals().isEmpty());
    }

    @Test
    void returningAVehicleThatIsNotOutIsRefused() {
        assertThrows(VehicleStateException.class, () -> depot.returnVehicle(vitz));

        assertEquals(VehicleStatus.AVAILABLE, vitz.getStatus());
    }

    @Test
    void invalidDaysAreRefusedBeforeAnythingChanges() {
        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, vitz, 0));

        assertEquals(VehicleStatus.AVAILABLE, vitz.getStatus());
        assertTrue(depot.getRentals().isEmpty());
    }

    @Test
    void vehicleOutsideTheFleetIsRefused() {
        Vehicle stranger = new EconomyVehicle("Kia Picanto", "GT-9999-21", 150.00, 500.00);

        assertThrows(IllegalArgumentException.class, () -> depot.rent(kofi, stranger, 1));
    }

    @Test
    void duplicateRegistrationIsRefusedIgnoringCase() {
        Vehicle copy = new EconomyVehicle("Other car", "gt-1234-20", 100.00, 100.00);

        assertThrows(IllegalArgumentException.class, () -> depot.addVehicle(copy));
        assertEquals(2, depot.getVehicles().size());
    }

    @Test
    void depotListsCannotBeModifiedByCallers() {
        depot.rent(kofi, vitz, 3);

        assertThrows(UnsupportedOperationException.class, () -> depot.getRentals().clear());
        assertThrows(UnsupportedOperationException.class, () -> depot.getVehicles().clear());
        assertThrows(UnsupportedOperationException.class, () -> depot.getActiveRentals().clear());
        assertThrows(UnsupportedOperationException.class, () -> depot.rentalsFor(kofi).clear());
    }

    @Test
    void depositsHeldCountsOnlyActiveRentals() {
        depot.rent(kofi, vitz, 3);
        depot.rent(ama, prado, 2);
        assertEquals(500.0, depot.getDepositsHeld());

        depot.returnVehicle(vitz);

        assertEquals(0.0, depot.getDepositsHeld());
    }
}
