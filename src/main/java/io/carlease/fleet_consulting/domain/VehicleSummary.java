package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.VehicleType;

public class VehicleSummary {

    private final String vin;
    private final Amount purchasePrice;
    private final double kwhPer100km;
    private final Kilometre rangeKm;
    private final String brand;
    private final String model;
    private final VehicleType vehicleType;
    private final boolean available;

    private VehicleSummary(String vin, Amount purchasePrice, double kwhPer100km, Kilometre rangeKm,
                           String brand, String model, VehicleType vehicleType, boolean available) {
        this.vin = vin;
        this.purchasePrice = purchasePrice;
        this.kwhPer100km = kwhPer100km;
        this.rangeKm = rangeKm;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.available = available;
    }

    public static VehicleSummary of(String vin, Amount purchasePrice, double kwhPer100km, Kilometre rangeKm,
                                    String brand, String model, VehicleType vehicleType, boolean available) {
        if (vin == null || vin.isBlank())
            throw new InvalidFleetAttributeException("vin", "must not be blank");
        if (purchasePrice == null)
            throw new InvalidFleetAttributeException("purchasePrice", "must not be null");
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
        return new VehicleSummary(vin, purchasePrice, kwhPer100km, rangeKm, brand, model, vehicleType, available);
    }

    public String getVin() {
        return vin;
    }

    public Amount getPurchasePrice() {
        return purchasePrice;
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
