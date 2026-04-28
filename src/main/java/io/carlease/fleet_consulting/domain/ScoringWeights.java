package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class ScoringWeights {

    private final int rangeWeight;
    private final int costWeight;
    private final int co2Weight;

    private ScoringWeights(int rangeWeight, int costWeight, int co2Weight) {
        this.rangeWeight = rangeWeight;
        this.costWeight = costWeight;
        this.co2Weight = co2Weight;
    }

    public static ScoringWeights of(int range, int cost, int co2) {
        if (range < 0 || cost < 0 || co2 < 0)
            throw new InvalidFleetAttributeException("weights", "must not be negative");
        if (range + cost + co2 != 100)
            throw new InvalidFleetAttributeException("weights", "must sum to 100");
        return new ScoringWeights(range, cost, co2);
    }

    public static ScoringWeights defaultWeights() {
        return new ScoringWeights(40, 40, 20);
    }

    public int getRangeWeight() {
        return rangeWeight;
    }

    public int getCostWeight() {
        return costWeight;
    }

    public int getCo2Weight() {
        return co2Weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ScoringWeights other = (ScoringWeights) obj;
        return rangeWeight == other.rangeWeight
                && costWeight == other.costWeight
                && co2Weight == other.co2Weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rangeWeight, costWeight, co2Weight);
    }
}