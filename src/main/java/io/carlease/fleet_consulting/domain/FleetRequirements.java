package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class FleetRequirements {

    private final Kilometre dailyRouteKm;
    private final NumberOfDrivers numberOfDrivers;
    private final ChargingSetup chargingSetup;
    private final Budget budget;
    private final VehicleType sizePreference;
    private final ScoringWeights scoringWeights;

    private FleetRequirements(Kilometre dailyRouteKm, NumberOfDrivers numberOfDrivers,
                               ChargingSetup chargingSetup, Budget budget, VehicleType sizePreference, ScoringWeights scoringWeights) {
        this.dailyRouteKm = dailyRouteKm;
        this.numberOfDrivers = numberOfDrivers;
        this.chargingSetup = chargingSetup;
        this.budget = budget;
        this.sizePreference = sizePreference;
        this.scoringWeights = scoringWeights;

    }

    public static FleetRequirements of(Kilometre dailyRouteKm, NumberOfDrivers numberOfDrivers,
                                        ChargingSetup chargingSetup, Budget budget, VehicleType sizePreference, ScoringWeights scoringWeights) {
        if (dailyRouteKm == null) throw new InvalidFleetAttributeException("dailyRouteKm", "must not be null");
        if (numberOfDrivers == null) throw new InvalidFleetAttributeException("numberOfDrivers", "must not be null");
        if (chargingSetup == null) throw new InvalidFleetAttributeException("chargingSetup", "must not be null");
        if (budget == null) throw new InvalidFleetAttributeException("budget", "must not be null");
        if (scoringWeights == null) throw new InvalidFleetAttributeException("scoringWeights", "must not be null");
        return new FleetRequirements(dailyRouteKm, numberOfDrivers, chargingSetup, budget, sizePreference, scoringWeights);
    }

    public Kilometre getDailyRouteKm() {
        return dailyRouteKm;
    }

    public NumberOfDrivers getNumberOfDrivers() {
        return numberOfDrivers;
    }

    public ChargingSetup getChargingSetup() {
        return chargingSetup;
    }

    public Budget getBudget() {
        return budget;
    }

    public VehicleType getSizePreference() {
        return sizePreference;
    }

    public ScoringWeights getScoringWeights() {
        return scoringWeights;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FleetRequirements other = (FleetRequirements) obj;
        return dailyRouteKm.equals(other.dailyRouteKm)
                && numberOfDrivers.equals(other.numberOfDrivers)
                && chargingSetup.equals(other.chargingSetup)
                && budget.equals(other.budget)
                && Objects.equals(sizePreference, other.sizePreference)
                && scoringWeights.equals(other.scoringWeights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyRouteKm, numberOfDrivers, chargingSetup, budget, sizePreference, scoringWeights);
    }
}
