package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class IndividualVehicleScore {

    private final double rangeScore;
    private final double co2Score;
    private final double costScore;
    private final double finalScore;

    private IndividualVehicleScore(double rangeScore, double co2Score, double costScore, double finalScore) {
        this.rangeScore = rangeScore;
        this.co2Score = co2Score;
        this.costScore = costScore;
        this.finalScore = finalScore;
    }

    public static IndividualVehicleScore of(double rangeScore, double co2Score, double costScore, ScoringWeights weights) {
        if (weights == null)
            throw new InvalidFleetAttributeException("weights", "must not be null");
        double finalScore = rangeScore * (weights.getRangeWeight() / 100.0)
                + co2Score * (weights.getCo2Weight() / 100.0)
                + costScore * (weights.getCostWeight() / 100.0);
        return new IndividualVehicleScore(rangeScore, co2Score, costScore, finalScore);
    }

    public double getRangeScore() {
        return rangeScore;
    }

    public double getCo2Score() {
        return co2Score;
    }

    public double getCostScore() {
        return costScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IndividualVehicleScore other = (IndividualVehicleScore) obj;
        return Double.compare(rangeScore, other.rangeScore) == 0
                && Double.compare(co2Score, other.co2Score) == 0
                && Double.compare(costScore, other.costScore) == 0
                && Double.compare(finalScore, other.finalScore) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rangeScore, co2Score, costScore, finalScore);
    }
}
