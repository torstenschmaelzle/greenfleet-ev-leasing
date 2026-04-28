package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.VehicleType;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.Optional;

@ValueObject
public class FleetRequirements {

    private final Kilometre dailyRouteKm;
    private final NumberOfVehicles numberOfVehicles;
    private final ChargingSetup chargingSetup;
    private final Optional<Budget> budget;
    private final LeaseTerm leaseTerm;
    private final Optional<String> brand;
    private final Optional<VehicleType> vehicleType;

    private FleetRequirements(Kilometre dailyRouteKm, NumberOfVehicles numberOfVehicles,
                               ChargingSetup chargingSetup, Optional<Budget> budget,
                               LeaseTerm leaseTerm, Optional<String> brand,
                               Optional<VehicleType> vehicleType) {
        this.dailyRouteKm = dailyRouteKm;
        this.numberOfVehicles = numberOfVehicles;
        this.chargingSetup = chargingSetup;
        this.budget = budget;
        this.leaseTerm = leaseTerm;
        this.brand = brand;
        this.vehicleType = vehicleType;
    }

    public static FleetRequirements of(Kilometre dailyRouteKm, NumberOfVehicles numberOfVehicles,
                                        ChargingSetup chargingSetup, Optional<Budget> budget,
                                        LeaseTerm leaseTerm, Optional<String> brand,
                                        Optional<VehicleType> vehicleType) {
        if (dailyRouteKm == null) throw new InvalidFleetAttributeException("dailyRouteKm", "must not be null");
        if (numberOfVehicles == null) throw new InvalidFleetAttributeException("numberOfVehicles", "must not be null");
        if (chargingSetup == null) throw new InvalidFleetAttributeException("chargingSetup", "must not be null");
        if (budget == null) throw new InvalidFleetAttributeException("budget", "must not be null");
        if (leaseTerm == null) throw new InvalidFleetAttributeException("leaseTerm", "must not be null");
        return new FleetRequirements(dailyRouteKm, numberOfVehicles, chargingSetup, budget, leaseTerm,
                brand == null ? Optional.empty() : brand,
                vehicleType == null ? Optional.empty() : vehicleType);
    }

    public Kilometre getDailyRouteKm() {
        return dailyRouteKm;
    }

    public NumberOfVehicles getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public ChargingSetup getChargingSetup() {
        return chargingSetup;
    }

    public Optional<Budget> getBudget() {
        return budget;
    }

    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public Optional<String> getBrand() {
        return brand;
    }

    public Optional<VehicleType> getVehicleType() {
        return vehicleType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FleetRequirements other = (FleetRequirements) obj;
        return dailyRouteKm.equals(other.dailyRouteKm)
                && numberOfVehicles.equals(other.numberOfVehicles)
                && chargingSetup.equals(other.chargingSetup)
                && budget.equals(other.budget)
                && leaseTerm.equals(other.leaseTerm)
                && brand.equals(other.brand)
                && vehicleType.equals(other.vehicleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyRouteKm, numberOfVehicles, chargingSetup, budget, leaseTerm, brand, vehicleType);
    }
}
