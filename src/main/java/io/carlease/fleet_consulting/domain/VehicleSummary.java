package io.carlease.fleet_consulting.domain;

public class VehicleSummary {

    private final Amount monthlyLeasePrice;
    private final double kwhPer100km;
    private final Kilometre rangeKm;

    private VehicleSummary(Amount monthlyLeasePrice, double kwhPer100km, Kilometre rangeKm) {
        this.monthlyLeasePrice = monthlyLeasePrice;
        this.kwhPer100km = kwhPer100km;
        this.rangeKm = rangeKm;
    }

    public static VehicleSummary of(Amount monthlyLeasePrice, double kwhPer100km, Kilometre rangeKm) {
        if (monthlyLeasePrice == null)
            throw new InvalidFleetAttributeException("monthlyLeasePrice", "must not be null");
        if (kwhPer100km <= 0)
            throw new InvalidFleetAttributeException("kwhPer100km", "must be greater than 0");
        if (rangeKm == null)
            throw new InvalidFleetAttributeException("rangeKm", "must not be null");
        return new VehicleSummary(monthlyLeasePrice, kwhPer100km, rangeKm);
    }

    public Amount getMonthlyLeasePrice() {
        return monthlyLeasePrice;
    }

    public double getKwhPer100km() {
        return kwhPer100km;
    }

    public Kilometre getRangeKm() {
        return rangeKm;
    }
}
