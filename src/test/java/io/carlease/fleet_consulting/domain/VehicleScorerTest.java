package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleScorerTest {

    private final VehicleScorer scorer = new VehicleScorer();

    // dailyRouteKm=100 → requiredKm = ceil(100 * 1.2) = 120
    private FleetRequirements req() {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(1),
                ChargingSetup.of(
                        ChargingType.COMPANY_CHARGER,
                        ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
                ),
                Optional.empty(),
                LeaseTerm.of(36),
                Optional.empty(),
                Optional.empty()
        );
    }

    private VehicleSummaryWithInstallment vehicle(String vin, int rangeKm,
                                                   double kwhPer100km, long installmentCents) {
        VehicleSummary summary = VehicleSummary.of(
                vin,
                Amount.ofCents(5000000, Currency.CHF),
                kwhPer100km,
                Kilometre.of(rangeKm),
                "Tesla",
                "Model 3",
                VehicleType.SUV,
                true
        );
        return VehicleSummaryWithInstallment.of(
                summary,
                Installment.of(Amount.ofCents(installmentCents, Currency.CHF))
        );
    }

    @Test
    void emptyListReturnsEmptyResult() {
        List<ScoredVehicle> result = scorer.score(List.of(), ScoringWeights.balanced(), req());

        assertThat(result).isEmpty();
    }

    @Test
    void singleVehicleGetsPerfectNormalizedScores() {
        // Only vehicle in list → rangeBuffer/maxRangeBuffer=1, co2/maxCo2=1, 1-installment/max=0
        List<ScoredVehicle> result = scorer.score(
                List.of(vehicle("TSTMODEL300000001", 300, 20.0, 50000)),
                ScoringWeights.of(100, 0, 0),
                req()
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getScore().getRangeScore()).isEqualTo(1.0);
    }

    @Nested
    class RelativeScoring {

        @Test
        void vehicleWithMoreRangeGetsHigherRangeScore() {
            // Vehicle A: 400 km range, Vehicle B: 200 km range (both > required 120)
            // rangeBuffer A = 280, rangeBuffer B = 80 → A gets higher rangeScore
            List<ScoredVehicle> result = scorer.score(
                    List.of(
                            vehicle("TSTMODEL300000001", 400, 20.0, 50000),
                            vehicle("TSTMODEL300000002", 200, 20.0, 50000)
                    ),
                    ScoringWeights.of(100, 0, 0),
                    req()
            );

            ScoredVehicle vehicleA = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000001"))
                    .findFirst().orElseThrow();
            ScoredVehicle vehicleB = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000002"))
                    .findFirst().orElseThrow();

            assertThat(vehicleA.getScore().getRangeScore())
                    .isGreaterThan(vehicleB.getScore().getRangeScore());
        }

        @Test
        void vehicleWithLowerInstallmentGetsHigherCostScore() {
            // Cheap: 20000 cents/month, Expensive: 80000 cents/month
            List<ScoredVehicle> result = scorer.score(
                    List.of(
                            vehicle("TSTMODEL300000001", 300, 20.0, 20000),
                            vehicle("TSTMODEL300000002", 300, 20.0, 80000)
                    ),
                    ScoringWeights.of(0, 0, 100),
                    req()
            );

            ScoredVehicle cheap = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000001"))
                    .findFirst().orElseThrow();
            ScoredVehicle expensive = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000002"))
                    .findFirst().orElseThrow();

            assertThat(cheap.getScore().getCostScore())
                    .isGreaterThan(expensive.getScore().getCostScore());
        }

        @Test
        void vehicleWithLowerKwhGetsHigherCo2Score() {
            // Efficient: 14 kWh/100km → more CO2 savings vs ICE → higher co2Score
            // Inefficient: 22 kWh/100km → fewer savings
            List<ScoredVehicle> result = scorer.score(
                    List.of(
                            vehicle("TSTMODEL300000001", 300, 14.0, 50000),
                            vehicle("TSTMODEL300000002", 300, 22.0, 50000)
                    ),
                    ScoringWeights.of(0, 100, 0),
                    req()
            );

            ScoredVehicle efficient = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000001"))
                    .findFirst().orElseThrow();
            ScoredVehicle inefficient = result.stream()
                    .filter(v -> v.getVehicle().getVehicleSummary().getVin().equals("TSTMODEL300000002"))
                    .findFirst().orElseThrow();

            assertThat(efficient.getScore().getCo2Score())
                    .isGreaterThan(inefficient.getScore().getCo2Score());
        }

        @Test
        void finalScoreIsWeightedSumOfIndividualScores() {
            // weights=(100,0,0) → finalScore = rangeScore * 1.0 + co2Score * 0 + costScore * 0
            List<ScoredVehicle> result = scorer.score(
                    List.of(vehicle("TSTMODEL300000001", 300, 20.0, 50000)),
                    ScoringWeights.of(100, 0, 0),
                    req()
            );

            IndividualVehicleScore score = result.get(0).getScore();
            assertThat(score.getFinalScore())
                    .isEqualTo(score.getRangeScore() * 1.0 + score.getCo2Score() * 0.0 + score.getCostScore() * 0.0);
        }
    }
}
