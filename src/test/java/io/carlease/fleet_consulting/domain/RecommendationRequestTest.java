package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecommendationRequestTest {

    private FleetRequirements validReq() {
        ChargingSetup setup = ChargingSetup.of(
                ChargingType.COMPANY_CHARGER,
                ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
        );
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfDrivers.of(1),
                setup,
                Budget.of(Amount.ofCents(80000, Currency.CHF)),
                VehicleType.SUV,
                ScoringWeights.defaultWeights()
        );
    }

    private VehicleSummary vehicleSummary(String brand, String model, boolean available) {
        return VehicleSummary.of(
                Amount.ofCents(50000, Currency.CHF),
                20.0,
                Kilometre.of(300),
                brand,
                model,
                VehicleType.SUV,
                available
        );
    }

    private VehicleSummary vehicleSummaryWithLease(String brand, String model, long leaseCents) {
        return VehicleSummary.of(
                Amount.ofCents(leaseCents, Currency.CHF),
                20.0,
                Kilometre.of(300),
                brand,
                model,
                VehicleType.SUV,
                true
        );
    }

    @Nested
    class Validation {

        @Test
        void requestIdMustNotBeNull() {
            assertThatThrownBy(() -> new RecommendationRequest(null, validReq(), new WeightedScoringStrategy()))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void requirementsMustNotBeNull() {
            assertThatThrownBy(() -> new RecommendationRequest(UUID.randomUUID(), null, new WeightedScoringStrategy()))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void scoringStrategyMustNotBeNull() {
            assertThatThrownBy(() -> new RecommendationRequest(UUID.randomUUID(), validReq(), null))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void generateRecommendationsWithNullListThrows() {
            RecommendationRequest request = new RecommendationRequest(UUID.randomUUID(), validReq(), new WeightedScoringStrategy());

            assertThatThrownBy(() -> request.generateRecommendations(null))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }

    @Nested
    class RecommendationGeneration {

        @Test
        void unavailableVehiclesAreExcluded() {
            RecommendationRequest request = new RecommendationRequest(UUID.randomUUID(), validReq(), new WeightedScoringStrategy());
            List<VehicleSummary> vehicles = List.of(
                    vehicleSummary("Tesla", "Model 3", true),
                    vehicleSummary("BMW", "iX3", false)
            );

            request.generateRecommendations(vehicles);

            assertThat(request.getRecommendations()).hasSize(1);
        }

        @Test
        void getTopRecommendationsSortsByScoreDescending() {
            // Cheap Car lease=20000: monthly=38000, budget=80000 → tcoScore=(1-38000/80000)*40=21 → score=81
            // Expensive Car lease=50000: monthly=68000, budget=80000 → tcoScore=(1-68000/80000)*40=6 → score=66
            RecommendationRequest request = new RecommendationRequest(UUID.randomUUID(), validReq(), new WeightedScoringStrategy());
            List<VehicleSummary> vehicles = List.of(
                    vehicleSummaryWithLease("BMW", "iX3", 50000),
                    vehicleSummaryWithLease("Tesla", "Model 3", 20000)
            );

            request.generateRecommendations(vehicles);
            List<VehicleRecommendation> top = request.getTopRecommendations(2);

            assertThat(top.get(0).getBrand()).isEqualTo("Tesla");
            assertThat(top.get(1).getBrand()).isEqualTo("BMW");
        }

        @Test
        void getBestMatchReturnsHighestScoredVehicle() {
            RecommendationRequest request = new RecommendationRequest(UUID.randomUUID(), validReq(), new WeightedScoringStrategy());
            List<VehicleSummary> vehicles = List.of(
                    vehicleSummaryWithLease("BMW", "iX3", 50000),
                    vehicleSummaryWithLease("Tesla", "Model 3", 20000)
            );

            request.generateRecommendations(vehicles);

            assertThat(request.getBestMatch().getBrand()).isEqualTo("Tesla");
        }

        @Test
        void getBestMatchThrowsWhenNoRecommendationsExist() {
            RecommendationRequest request = new RecommendationRequest(UUID.randomUUID(), validReq(), new WeightedScoringStrategy());

            assertThatThrownBy(request::getBestMatch)
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }
}
