package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.BundleType;
import org.jmolecules.ddd.annotation.Entity;

import java.util.List;

@Entity
public class FleetBundle {

    private final BundleType bundleType;
    private final List<BundleVehicle> vehicles;
    private final TotalCostOfOwnership tco;
    private final double totalCo2SavingsKgPerYear;
    private final double bundleScore;

    private FleetBundle(BundleType bundleType, List<BundleVehicle> vehicles,
                        TotalCostOfOwnership tco, double totalCo2SavingsKgPerYear, double bundleScore) {
        this.bundleType = bundleType;
        this.vehicles = vehicles;
        this.tco = tco;
        this.totalCo2SavingsKgPerYear = totalCo2SavingsKgPerYear;
        this.bundleScore = bundleScore;
    }

    public static FleetBundle of(BundleType bundleType, List<BundleVehicle> vehicles,
                                  TotalCostOfOwnership tco, double totalCo2SavingsKgPerYear,
                                  double bundleScore) {
        if (bundleType == null)
            throw new InvalidFleetAttributeException("bundleType", "must not be null");
        if (vehicles == null || vehicles.isEmpty())
            throw new InvalidFleetAttributeException("vehicles", "must not be empty");
        if (tco == null)
            throw new InvalidFleetAttributeException("tco", "must not be null");
        return new FleetBundle(bundleType, List.copyOf(vehicles), tco, totalCo2SavingsKgPerYear, bundleScore);
    }

    public BundleType getBundleType() {
        return bundleType;
    }

    public List<BundleVehicle> getVehicles() {
        return vehicles;
    }

    public TotalCostOfOwnership getTco() {
        return tco;
    }

    public double getTotalCo2SavingsKgPerYear() {
        return totalCo2SavingsKgPerYear;
    }

    public double getBundleScore() {
        return bundleScore;
    }
}
