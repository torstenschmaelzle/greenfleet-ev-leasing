package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class SuitabilityScore {

    private final int score;

    private SuitabilityScore(int score) {
        this.score = score;
    }

    public static SuitabilityScore calculate(RangeFit rangeFit, TotalCostOfOwnership tco,
                                              Budget budget, Co2Impact co2) {
        if (rangeFit == null) throw new InvalidFleetAttributeException("rangeFit", "must not be null");
        if (tco == null) throw new InvalidFleetAttributeException("tco", "must not be null");
        if (budget == null) throw new InvalidFleetAttributeException("budget", "must not be null");
        if (co2 == null) throw new InvalidFleetAttributeException("co2", "must not be null");

        double rangeScore;
        if (rangeFit.isSufficient()) {
            rangeScore = 40.0;
        } else {
            rangeScore = ((double) rangeFit.getActualKm().getValue() / rangeFit.getRequiredKm().getValue()) * 40.0;
        }

        double tcoScore;
        long totalCents = tco.getMonthlyTotal().getAmountInCents();
        long budgetCents = budget.getMonthlyMax().getAmountInCents();
        if (budgetCents > 0 && totalCents <= budgetCents) {
            tcoScore = (1.0 - (double) totalCents / budgetCents) * 40.0;
        } else {
            tcoScore = 0.0;
        }

        double evGrams = co2.getEvGramsPerKm().doubleValue();
        double co2Score = Math.max(0.0, (1.0 - evGrams / DomainConstants.ICE_GRAMS_PER_KM) * 20.0);

        int finalScore = (int) Math.min(100, Math.max(0, Math.round(rangeScore + tcoScore + co2Score)));
        return new SuitabilityScore(finalScore);
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SuitabilityScore other = (SuitabilityScore) obj;
        return score == other.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score);
    }
}
