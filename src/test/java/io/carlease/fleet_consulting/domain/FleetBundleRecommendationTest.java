package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FleetBundleRecommendationTest {

    // dailyRouteKm=100 → required range = ceil(100 * 1.2) = 120 km
    private static final Kilometre SUFFICIENT_RANGE = Kilometre.of(300);
    private static final Kilometre INSUFFICIENT_RANGE = Kilometre.of(100);

    private FleetRequirements req(int numberOfVehicles) {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(numberOfVehicles),
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

    private VehicleSummaryWithInstallment vehicle(String vin, Kilometre range, boolean available) {
        VehicleSummary summary = VehicleSummary.of(
                vin,
                Amount.ofCents(5000000, Currency.CHF),
                20.0,
                range,
                "Tesla",
                "Model 3",
                VehicleType.SUV,
                available
        );
        return VehicleSummaryWithInstallment.of(
                summary,
                Installment.of(Amount.ofCents(50000, Currency.CHF))
        );
    }

    @Nested
    class Validation {

        @Test
        void requirementsMustNotBeNull() {
            assertThatThrownBy(() -> FleetBundleRecommendation.generate(
                    null, List.of(), Optional.empty()))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void vehiclesMustNotBeNull() {
            assertThatThrownBy(() -> FleetBundleRecommendation.generate(
                    req(1), null, Optional.empty()))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void customWeightsMustNotBeNull() {
            assertThatThrownBy(() -> FleetBundleRecommendation.generate(
                    req(1), List.of(), null))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }

    @Nested
    class HardFilters {

        @Test
        void unavailableVehiclesAreExcluded() {
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true),
                    vehicle("TSTMODEL300000002", SUFFICIENT_RANGE, false)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.empty());

            // only 1 available vehicle → bundles use only that one
            result.getBundles().forEach(bundle ->
                    assertThat(bundle.getVehicles()).allMatch(v -> v.getVin().equals("TSTMODEL300000001"))
            );
        }

        @Test
        void vehiclesWithInsufficientRangeAreExcluded() {
            // dailyRouteKm=100 → required=120, vehicle has only 100 km range
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", INSUFFICIENT_RANGE, true)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.empty());

            assertThat(result.getBundles()).isEmpty();
        }

        @Test
        void brandFilterKeepsOnlyMatchingBrand() {
            FleetRequirements bmwReq = FleetRequirements.of(
                    Kilometre.of(100),
                    NumberOfVehicles.of(1),
                    ChargingSetup.of(
                            ChargingType.COMPANY_CHARGER,
                            ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
                    ),
                    Optional.empty(),
                    LeaseTerm.of(36),
                    Optional.of("BMW"),
                    Optional.empty()
            );

            VehicleSummaryWithInstallment tesla = VehicleSummaryWithInstallment.of(
                    VehicleSummary.of("TSTMODEL300000001", Amount.ofCents(5000000, Currency.CHF),
                            20.0, SUFFICIENT_RANGE, "Tesla", "Model 3", VehicleType.SUV, true),
                    Installment.of(Amount.ofCents(50000, Currency.CHF))
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    bmwReq, List.of(tesla), Optional.empty());

            assertThat(result.getBundles()).isEmpty();
        }
    }

    @Nested
    class BundleGeneration {

        @Test
        void withoutCustomWeightsNoBundleHasTypeCustom() {
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.empty());

            assertThat(result.getBundles())
                    .noneMatch(b -> b.getBundleType() == BundleType.CUSTOM);
        }

        @Test
        void withCustomWeightsCustomBundleIsPresent() {
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true)
            );
            ScoringWeights custom = ScoringWeights.of(50, 30, 20);

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.of(custom));

            assertThat(result.getBundles())
                    .anyMatch(b -> b.getBundleType() == BundleType.CUSTOM);
        }

        @Test
        void generatesUpToThreePredefinedBundles() {
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.empty());

            long predefinedCount = result.getBundles().stream()
                    .filter(b -> b.getBundleType() != BundleType.CUSTOM)
                    .count();
            assertThat(predefinedCount).isLessThanOrEqualTo(3);
        }

        @Test
        void eachBundleContainsExactlyNumberOfVehicles() {
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true),
                    vehicle("TSTMODEL300000002", SUFFICIENT_RANGE, true),
                    vehicle("TSTMODEL300000003", SUFFICIENT_RANGE, true)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(2), vehicles, Optional.empty());

            result.getBundles().forEach(bundle ->
                    assertThat(bundle.getVehicles()).hasSize(2)
            );
        }

        @Test
        void identicalBundlesAreDeduplicated() {
            // Only 1 vehicle → all bundles pick the same car → deduplicated to 1
            List<VehicleSummaryWithInstallment> vehicles = List.of(
                    vehicle("TSTMODEL300000001", SUFFICIENT_RANGE, true)
            );

            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), vehicles, Optional.empty());

            assertThat(result.getBundles()).hasSize(1);
        }

        @Test
        void requestIdIsNotNull() {
            FleetBundleRecommendation result = FleetBundleRecommendation.generate(
                    req(1), List.of(), Optional.empty());

            assertThat(result.getRequestId()).isNotNull();
        }
    }
}
