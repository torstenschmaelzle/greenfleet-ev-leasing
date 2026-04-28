package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.VehicleType;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class BundleVehicle {

    private final String vin;
    private final String brand;
    private final String model;
    private final VehicleType vehicleType;
    private final int rangeKm;
    private final Installment installment;
    private final double co2SavingsKgPerYear;
    private final IndividualVehicleScore score;

    private BundleVehicle(String vin, String brand, String model, VehicleType vehicleType,
                          int rangeKm, Installment installment, double co2SavingsKgPerYear,
                          IndividualVehicleScore score) {
        this.vin = vin;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.rangeKm = rangeKm;
        this.installment = installment;
        this.co2SavingsKgPerYear = co2SavingsKgPerYear;
        this.score = score;
    }

    public static BundleVehicle of(String vin, String brand, String model, VehicleType vehicleType,
                                   int rangeKm, Installment installment, double co2SavingsKgPerYear,
                                   IndividualVehicleScore score) {
        if (vin == null || vin.isBlank())
            throw new InvalidFleetAttributeException("vin", "must not be blank");
        if (brand == null || brand.isBlank())
            throw new InvalidFleetAttributeException("brand", "must not be blank");
        if (model == null || model.isBlank())
            throw new InvalidFleetAttributeException("model", "must not be blank");
        if (vehicleType == null)
            throw new InvalidFleetAttributeException("vehicleType", "must not be null");
        if (installment == null)
            throw new InvalidFleetAttributeException("installment", "must not be null");
        if (score == null)
            throw new InvalidFleetAttributeException("score", "must not be null");
        return new BundleVehicle(vin, brand, model, vehicleType, rangeKm, installment, co2SavingsKgPerYear, score);
    }

    public String getVin() {
        return vin;
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

    public int getRangeKm() {
        return rangeKm;
    }

    public Installment getInstallment() {
        return installment;
    }

    public double getCo2SavingsKgPerYear() {
        return co2SavingsKgPerYear;
    }

    public IndividualVehicleScore getScore() {
        return score;
    }
}
