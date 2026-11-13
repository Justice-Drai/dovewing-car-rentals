package com.dovewing.domain;

public final class EconomyVehicle extends Vehicle {

    private static final String CATEGORY = "ECONOMY";

    public EconomyVehicle(String model, String reg, double dailyRate, double baseDeposit) {
        super(model, reg, dailyRate, baseDeposit);
    }

    @Override
    public double depositFor(Renter renter) {
        return standardDeposit(renter);
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
