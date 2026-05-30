package io.carlease.fleet_consulting.application.service;

import io.carlease.ev_catalogue.application.port.in.CatalogueVehicleView;
import io.carlease.ev_catalogue.application.port.out.ListCataloguePort;
import io.carlease.ev_catalogue.application.service.ListCatalogueService;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.fleet_consulting.adapter.out.catalogue.EVCatalogueVehicleAdapter;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import io.carlease.shared.domain.VehicleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

/**
 * Task 7.2 – Bounded Context Integration Testing
 *
 * Caller:  GetRecommendationService     (Fleet Consulting bounded context)
 * Callee:  ListCatalogueService         (EV Catalogue bounded context)
 * Bridge:  EVCatalogueVehicleAdapter    (Fleet Consulting → EV Catalogue adapter)
 */
class GetRecommendationBoundedContextIntegrationTest {

    private static final String VIN = "WBA00000000000201";

    private FleetRequirements defaultRequirements() {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(1),
                ChargingSetup.of(
                        ChargingType.COMPANY_CHARGER,
                        ElectricityRate.of(Amount.ofCents(30L, Currency.CHF))
                ),
                Optional.of(Budget.of(Amount.ofCents(200000L, Currency.CHF))),
                LeaseTerm.of(36),
                Optional.empty(),
                Optional.empty()
        );
    }

    private VehicleSummary availableVehicleSummary(String vin) {
        return VehicleSummary.of(
                vin,
                Amount.ofCents(75000L, Currency.CHF),
                17.0,
                Kilometre.of(440),
                "BMW", "iX3", VehicleType.SUV,
                true
        );
    }

    private Vehicle catalogueVehicle(String vin) {
        return VehicleFactory.createVehicle(
                VehicleId.of(vin),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
    }


    // Test 1 – Caller isolation
    // Verifies that GetRecommendationService calls GetVehiclesPort.getVehicles()
    // and that the returned VehicleSummaries are reflected in the recommendation.

    @Nested
    class CallerInvokesCalleeWithCorrectData {

        private final GetVehiclesPort getVehiclesPort = mock(GetVehiclesPort.class);
        private final GetRecommendationService caller =
                new GetRecommendationService(getVehiclesPort);

        @Test
        void callerCallsGetVehiclesAndReflectsReturnedVehiclesInRecommendation() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(availableVehicleSummary(VIN)));

            FleetBundleRecommendation result = caller.recommend(defaultRequirements(), Optional.empty());

            then(getVehiclesPort).should().getVehicles();
            List<String> vins = result.getBundles().stream()
                    .flatMap(b -> b.getVehicles().stream())
                    .map(BundleVehicle::getVin)
                    .distinct()
                    .toList();
            assertThat(vins).contains(VIN);
        }
    }


    // Test 2 – Callee isolation
    // Verifies that ListCatalogueService correctly processes the input from the
    // port and returns the vehicle list as-is to the caller.

    @Nested
    class CalleeProcessesInputCorrectly {

        private final ListCataloguePort listCataloguePort = mock(ListCataloguePort.class);
        private final ListCatalogueService callee = new ListCatalogueService(listCataloguePort);

        @Test
        void calleeReturnsExactlyTheVehiclesProvidedByItsPort() {
            List<Vehicle> catalogue = List.of(catalogueVehicle(VIN));
            given(listCataloguePort.listAll()).willReturn(catalogue);

            List<CatalogueVehicleView> result = callee.listSummaries();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).vin()).isEqualTo(VIN);
        }
    }


    // Test 3 – End-to-end integration
    // Wires real implementations of all three classes together.
    // Only the infrastructure port (ListCataloguePort) is mocked.
    // Asserts that the VIN from the catalogue reaches the caller's
    // FleetBundleRecommendation return value.

    @Nested
    class EndToEndIntegration {

        private final ListCataloguePort listCataloguePort = mock(ListCataloguePort.class);

        private final ListCatalogueService listCatalogueService =
                new ListCatalogueService(listCataloguePort);
        private final EVCatalogueVehicleAdapter adapter =
                new EVCatalogueVehicleAdapter(listCatalogueService);
        private final GetRecommendationService caller =
                new GetRecommendationService(adapter);

        @Test
        void vehicleFromCatalogueAppearsInRecommendationReturnValue() {
            given(listCataloguePort.listAll()).willReturn(List.of(catalogueVehicle(VIN)));

            FleetBundleRecommendation result = caller.recommend(defaultRequirements(), Optional.empty());

            List<String> vins = result.getBundles().stream()
                    .flatMap(b -> b.getVehicles().stream())
                    .map(BundleVehicle::getVin)
                    .distinct()
                    .toList();
            assertThat(vins).contains(VIN);
        }
    }
}
