package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class ScoredVehicle {

    private final VehicleSummaryWithInstallment vehicle;
    private final IndividualVehicleScore score;

    private ScoredVehicle(VehicleSummaryWithInstallment vehicle, IndividualVehicleScore score) {
        this.vehicle = vehicle;
        this.score = score;
    }

    public static ScoredVehicle of(VehicleSummaryWithInstallment vehicle, IndividualVehicleScore score) {
        if (vehicle == null)
            throw new InvalidFleetAttributeException("vehicle", "must not be null");
        if (score == null)
            throw new InvalidFleetAttributeException("score", "must not be null");
        return new ScoredVehicle(vehicle, score);
    }

    public VehicleSummaryWithInstallment getVehicle() {
        return vehicle;
    }

    public IndividualVehicleScore getScore() {
        return score;
    }
}
