package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

public class VehicleSummary {

    private final Amount monthlyLeasePrice;
    private final double kwhPer100km;
    private final Kilometre rangeKm;
    private final String brand;
    private final String model;
    private final VehicleType vehicleType;
    private final boolean available;

    private VehicleSummary(Amount monthlyLeasePrice, double kwhPer100km, Kilometre rangeKm, String brand, String model, VehicleType vehicleType, boolean available) {
        this.monthlyLeasePrice = monthlyLeasePrice;
        this.kwhPer100km = kwhPer100km;
        this.rangeKm = rangeKm;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.available = available;
    }

    public static VehicleSummary of(Amount monthlyLeasePrice, double kwhPer100km, Kilometre rangeKm, String brand, String model, VehicleType vehicleType, boolean available) {
        if (monthlyLeasePrice == null)
            throw new InvalidFleetAttributeException("monthlyLeasePrice", "must not be null");
        if (kwhPer100km <= 0)
            throw new InvalidFleetAttributeException("kwhPer100km", "must be greater than 0");
        if (rangeKm == null)
            throw new InvalidFleetAttributeException("rangeKm", "must not be null");
        if (brand == null || brand.isBlank())
            throw new InvalidFleetAttributeException("brand", "must not be blank");
        if (model == null || model.isBlank())
            throw new InvalidFleetAttributeException("model", "must not be blank");
        if (vehicleType == null)
            throw new InvalidFleetAttributeException("vehicleType", "must not be null");
        return new VehicleSummary(monthlyLeasePrice, kwhPer100km, rangeKm, brand, model, vehicleType, available);
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

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isAvailable() {
        return available;
    }
}
