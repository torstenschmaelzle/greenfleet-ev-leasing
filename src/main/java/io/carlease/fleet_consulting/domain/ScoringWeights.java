package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class ScoringWeights {

    private final int rangeWeight;
    private final int co2Weight;
    private final int costWeight;

    private ScoringWeights(int rangeWeight, int co2Weight, int costWeight) {
        this.rangeWeight = rangeWeight;
        this.co2Weight = co2Weight;
        this.costWeight = costWeight;
    }

    public static ScoringWeights of(int rangeWeight, int co2Weight, int costWeight) {
        if (rangeWeight < 0 || co2Weight < 0 || costWeight < 0)
            throw new InvalidFleetAttributeException("weights", "must not be negative");
        if (rangeWeight + co2Weight + costWeight != 100)
            throw new InvalidFleetAttributeException("weights", "must sum to 100");
        return new ScoringWeights(rangeWeight, co2Weight, costWeight);
    }

    public static ScoringWeights balanced() {
        return new ScoringWeights(
                DomainConstants.BALANCED_RANGE_W,
                DomainConstants.BALANCED_CO2_W,
                DomainConstants.BALANCED_COST_W
        );
    }

    public static ScoringWeights eco() {
        return new ScoringWeights(
                DomainConstants.ECO_RANGE_W,
                DomainConstants.ECO_CO2_W,
                DomainConstants.ECO_COST_W
        );
    }

    public static ScoringWeights economic() {
        return new ScoringWeights(
                DomainConstants.ECONOMIC_RANGE_W,
                DomainConstants.ECONOMIC_CO2_W,
                DomainConstants.ECONOMIC_COST_W
        );
    }

    public static ScoringWeights premium() {
        return new ScoringWeights(
                DomainConstants.PREMIUM_RANGE_W,
                DomainConstants.PREMIUM_CO2_W,
                DomainConstants.PREMIUM_COST_W
        );
    }

    public int getRangeWeight() {
        return rangeWeight;
    }

    public int getCo2Weight() {
        return co2Weight;
    }

    public int getCostWeight() {
        return costWeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ScoringWeights other = (ScoringWeights) obj;
        return rangeWeight == other.rangeWeight
                && co2Weight == other.co2Weight
                && costWeight == other.costWeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rangeWeight, co2Weight, costWeight);
    }
}
