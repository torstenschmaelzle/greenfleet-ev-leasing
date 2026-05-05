package io.carlease.fleet_consulting.application.service;

import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GetRecommendationServiceTest {

    private final GetVehiclesPort getVehiclesPort = mock(GetVehiclesPort.class);
    private final GetRecommendationService service = new GetRecommendationService(getVehiclesPort);

    // -- domain object helpers --

    private FleetRequirements defaultRequirements() {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfDrivers.of(2),
                ChargingSetup.of(
                        ChargingType.COMPANY_CHARGER,
                        ElectricityRate.of(Amount.ofCents(30L, Currency.CHF))
                ),
                Budget.of(Amount.ofCents(200000L, Currency.CHF)),
                null,
                ScoringWeights.defaultWeights()
        );
    }

    private VehicleSummary availableVehicle(String brand, String model, long leasePriceCents,
                                             int rangeKm, double kwhPer100km) {
        return VehicleSummary.of(
                Amount.ofCents(leasePriceCents, Currency.CHF),
                kwhPer100km,
                Kilometre.of(rangeKm),
                brand, model, VehicleType.SEDAN,
                true
        );
    }

    private VehicleSummary unavailableVehicle() {
        return VehicleSummary.of(
                Amount.ofCents(70000L, Currency.CHF),
                18.0,
                Kilometre.of(450),
                "VW", "ID.4", VehicleType.SUV,
                false
        );
    }

    // -- tests --

    @Nested
    class whenVehiclesAreAvailable {

        @Test
        void recommend_returnsOneRecommendationPerAvailableVehicle() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("BMW", "i3", 60000L, 460, 17.0),
                    availableVehicle("Tesla", "Model 3", 80000L, 500, 15.0)
            ));

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result).hasSize(2);
        }

        @Test
        void recommend_returnsResultsRankedByScoreDescending() {
            // BMW (60000 cents/month) scores higher than Tesla (150000 cents/month)
            // due to lower TCO cost relative to 2000 CHF budget (weights 40/40/20)
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("Tesla", "Model 3", 150000L, 500, 15.0),
                    availableVehicle("BMW", "i3", 60000L, 460, 17.0)
            ));

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result.get(0).getBrand()).isEqualTo("BMW");
            assertThat(result.get(1).getBrand()).isEqualTo("Tesla");
        }

        @Test
        void recommend_setsCorrectBrandAndModelOnRecommendation() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("BMW", "i3", 60000L, 460, 17.0)
            ));

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result.get(0).getBrand()).isEqualTo("BMW");
            assertThat(result.get(0).getModel()).isEqualTo("i3");
        }
    }

    @Nested
    class whenSomeVehiclesAreUnavailable {

        @Test
        void recommend_filtersOutUnavailableVehicles() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("BMW", "i3", 60000L, 460, 17.0),
                    unavailableVehicle()
            ));

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBrand()).isEqualTo("BMW");
        }

        @Test
        void recommend_returnsEmptyListWhenAllVehiclesUnavailable() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    unavailableVehicle()
            ));

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class whenNoCatalogueVehiclesExist {

        @Test
        void recommend_returnsEmptyList() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of());

            List<VehicleRecommendation> result = service.recommend(defaultRequirements());

            assertThat(result).isEmpty();
        }
    }
}
