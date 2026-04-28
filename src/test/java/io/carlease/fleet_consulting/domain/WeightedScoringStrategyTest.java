package io.carlease.fleet_consulting.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeightedScoringStrategyTest {

    private final WeightedScoringStrategy strategy = new WeightedScoringStrategy();

    private FleetRequirements reqWithWeightsAndBudget(ScoringWeights weights, long budgetCents) {
        ChargingSetup setup = ChargingSetup.of(
                ChargingType.COMPANY_CHARGER,
                ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
        );
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfDrivers.of(1),
                setup,
                Budget.of(Amount.ofCents(budgetCents, Currency.CHF)),
                VehicleType.SUV,
                weights
        );
    }

    @Nested
    class RangeScoring {

        @Test
        void scoreIsMaxRangeWeightWhenRangeIsSufficient() {
            // weights=(100,0,0), dailyKm=100 → required=120, vehicleRange=200 → sufficient
            // rangeScore=100, tcoScore=0, co2Score=0 → finalScore=100
            FleetRequirements req = reqWithWeightsAndBudget(ScoringWeights.of(100, 0, 0), 80000);
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(200));
            TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(200));
            Co2Impact co2 = Co2Impact.calculate(req, 20.0);

            SuitabilityScore score = strategy.calculate(rangeFit, tco, co2, req);

            assertThat(score.getScore()).isEqualTo(100);
        }

        @Test
        void scoreIsProportionalWhenRangeIsInsufficient() {
            // weights=(100,0,0), dailyKm=100 → required=120, vehicleRange=60
            // rangeScore = (60/120) * 100 = 50 → finalScore=50
            FleetRequirements req = reqWithWeightsAndBudget(ScoringWeights.of(100, 0, 0), 80000);
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(60));
            TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(60));
            Co2Impact co2 = Co2Impact.calculate(req, 20.0);

            SuitabilityScore score = strategy.calculate(rangeFit, tco, co2, req);

            assertThat(score.getScore()).isEqualTo(50);
        }
    }

    @Nested
    class CostScoring {

        @Test
        void zeroCostScoreWhenMonthlyTotalExceedsBudget() {
            // weights=(0,100,0), lease=100000, charging=18000, monthly=118000 > budget=80000
            // tcoScore=0 → finalScore=0
            FleetRequirements req = reqWithWeightsAndBudget(ScoringWeights.of(0, 100, 0), 80000);
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(200));
            TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, Amount.ofCents(100000, Currency.CHF), 20.0, Kilometre.of(200));
            Co2Impact co2 = Co2Impact.calculate(req, 20.0);

            SuitabilityScore score = strategy.calculate(rangeFit, tco, co2, req);

            assertThat(score.getScore()).isEqualTo(0);
        }
    }

    @Nested
    class Co2Scoring {

        @Test
        void zeroCo2ScoreWhenEvEmissionsExceedIceBaseline() {
            // weights=(0,0,100), very high kwhPer100km → evGrams > ICE baseline (150 g/km)
            // kwhPer100km=2000 → evGrams = 2000*12/100 = 240 > 150 → co2Score clamped to 0
            FleetRequirements req = reqWithWeightsAndBudget(ScoringWeights.of(0, 0, 100), 80000);
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(200));
            TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, Amount.ofCents(50000, Currency.CHF), 2000.0, Kilometre.of(200));
            Co2Impact co2 = Co2Impact.calculate(req, 2000.0);

            SuitabilityScore score = strategy.calculate(rangeFit, tco, co2, req);

            assertThat(score.getScore()).isEqualTo(0);
        }
    }

    @Test
    void combinedScoreWithDefaultWeights() {
        // weights=(40,40,20), dailyKm=100, vehicleRange=200 → sufficient → rangeScore=40
        // lease=50000, charging=600*30=18000, monthly=68000, budget=80000
        //   tcoScore = (1 - 68000/80000) * 40 = 0.15 * 40 = 6.0
        // kwhPer100km=20, evGrams=2.4
        //   co2Score = (1 - 2.4/150) * 20 = 0.984 * 20 = 19.68
        // finalScore = round(40 + 6.0 + 19.68) = round(65.68) = 66
        FleetRequirements req = reqWithWeightsAndBudget(ScoringWeights.defaultWeights(), 80000);
        RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(200));
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(200));
        Co2Impact co2 = Co2Impact.calculate(req, 20.0);

        SuitabilityScore score = strategy.calculate(rangeFit, tco, co2, req);

        assertThat(score.getScore()).isEqualTo(66);
    }
}
