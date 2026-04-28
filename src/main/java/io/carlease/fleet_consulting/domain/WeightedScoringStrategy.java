package io.carlease.fleet_consulting.domain;

public class WeightedScoringStrategy implements ScoringStrategy {

    @Override
    public SuitabilityScore calculate(RangeFit rangeFit,
                                      TotalCostOfOwnership tco,
                                      Co2Impact co2Impact,
                                      FleetRequirements requirements) {

        ScoringWeights weights = requirements.getScoringWeights();

        double rangeScore;
        if (rangeFit.isSufficient()) {
            rangeScore = weights.getRangeWeight();
        } else {
            rangeScore = ((double) rangeFit.getActualKm().getValue()
                    / rangeFit.getRequiredKm().getValue()) * weights.getRangeWeight();
        }

        double tcoScore;
        long totalCents = tco.getMonthlyTotal().getAmountInCents();
        long budgetCents = requirements.getBudget().getMonthlyMax().getAmountInCents();
        if (budgetCents > 0 && totalCents <= budgetCents) {
            tcoScore = (1.0 - (double) totalCents / budgetCents) * weights.getCostWeight();
        } else {
            tcoScore = 0.0;
        }

        double evGrams = co2Impact.getEvGramsPerKm().doubleValue();
        double co2Score = Math.max(0.0,
                (1.0 - evGrams / DomainConstants.ICE_GRAMS_PER_KM) * weights.getCo2Weight());

        int finalScore = (int) Math.min(100, Math.max(0,
                Math.round(rangeScore + tcoScore + co2Score)));

        return new SuitabilityScore(finalScore);
    }
}