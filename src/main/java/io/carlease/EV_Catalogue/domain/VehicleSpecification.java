package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class VehicleSpecification {

    private final String brand;
    private final String model;
    private final Range rangeKm;
    private final VehicleType vehicleType;
    private final EnergyConsumption consumptionKwhPer100km;

    private VehicleSpecification(String brand, String model, Range rangeKm,
                                  VehicleType vehicleType, EnergyConsumption consumptionKwhPer100km) {
        this.brand = brand;
        this.model = model;
        this.rangeKm = rangeKm;
        this.vehicleType = vehicleType;
        this.consumptionKwhPer100km = consumptionKwhPer100km;
    }

    public static VehicleSpecification of(String brand, String model, Range rangeKm,
                                           VehicleType vehicleType, EnergyConsumption consumptionKwhPer100km) {
        if (brand == null || brand.isBlank()) {
            throw new InvalidVehicleAttributeException("brand", "must not be null or blank");
        }
        if (model == null || model.isBlank()) {
            throw new InvalidVehicleAttributeException("model", "must not be null or blank");
        }
        if (rangeKm == null) {
            throw new InvalidVehicleAttributeException("rangeKm", "must not be null");
        }
        if (vehicleType == null) {
            throw new InvalidVehicleAttributeException("vehicleType", "must not be null");
        }
        if (consumptionKwhPer100km == null) {
            throw new InvalidVehicleAttributeException("consumptionKwhPer100km", "must not be null");
        }
        return new VehicleSpecification(brand, model, rangeKm, vehicleType, consumptionKwhPer100km);
    }

    public Range getRangeKm() {
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

    public EnergyConsumption getConsumptionKwhPer100km() {
        return consumptionKwhPer100km;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VehicleSpecification other = (VehicleSpecification) obj;
        return brand.equals(other.brand)
                && model.equals(other.model)
                && rangeKm.equals(other.rangeKm)
                && vehicleType == other.vehicleType
                && consumptionKwhPer100km.equals(other.consumptionKwhPer100km);
    }
}
