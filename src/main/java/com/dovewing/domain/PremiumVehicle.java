package com.dovewing.domain;

public final class PremiumVehicle extends Vehicle {

    private static final String CATEGORY = "PREMIUM";

    public PremiumVehicle(String model, String reg, double dailyRate, double baseDeposit) {
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
