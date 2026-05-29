package io.carlease.fleet_consulting.application.service;

import io.carlease.fleet_consulting.application.port.out.CalculateInstallmentPort;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import io.carlease.shared.domain.VehicleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GetRecommendationServiceTest {

    private final GetVehiclesPort getVehiclesPort = mock(GetVehiclesPort.class);
    private final CalculateInstallmentPort calculateInstallmentPort = mock(CalculateInstallmentPort.class);
    private final GetRecommendationService service = new GetRecommendationService(getVehiclesPort, calculateInstallmentPort);

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

    private VehicleSummary availableVehicle(String vin, String brand, String model,
                                             long basePriceCents, int rangeKm) {
        return VehicleSummary.of(
                vin,
                Amount.ofCents(basePriceCents, Currency.CHF),
                17.0,
                Kilometre.of(rangeKm),
                brand, model, VehicleType.SEDAN,
                true
        );
    }

    private VehicleSummary unavailableVehicle() {
        return VehicleSummary.of(
                "WBA99999999999999",
                Amount.ofCents(70000L, Currency.CHF),
                18.0,
                Kilometre.of(450),
                "VW", "ID.4", VehicleType.SUV,
                false
        );
    }

    @Nested
    class whenVehiclesAreAvailable {

        @Test
        void recommend_generatesBundles() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("WBA00000000000001", "BMW", "i3", 60000L, 460)
            ));
            given(calculateInstallmentPort.calculateInstallment(any(), any()))
                    .willReturn(Installment.of(Amount.ofCents(60000L, Currency.CHF)));

            FleetBundleRecommendation result = service.recommend(defaultRequirements(), Optional.empty());

            assertThat(result.getBundles()).isNotEmpty();
        }

        @Test
        void recommend_callsInstallmentPortForEachVehicle() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("WBA00000000000001", "BMW", "i3", 60000L, 460),
                    availableVehicle("WBA00000000000002", "Tesla", "Model 3", 80000L, 500)
            ));
            given(calculateInstallmentPort.calculateInstallment(any(), any()))
                    .willReturn(Installment.of(Amount.ofCents(60000L, Currency.CHF)));

            service.recommend(defaultRequirements(), Optional.empty());

            verify(calculateInstallmentPort, times(2)).calculateInstallment(any(), any());
        }

        @Test
        void recommend_bundlesContainAvailableVehicle() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("WBA00000000000001", "BMW", "i3", 60000L, 460)
            ));
            given(calculateInstallmentPort.calculateInstallment(any(), any()))
                    .willReturn(Installment.of(Amount.ofCents(60000L, Currency.CHF)));

            FleetBundleRecommendation result = service.recommend(defaultRequirements(), Optional.empty());

            List<String> allVins = result.getBundles().stream()
                    .flatMap(b -> b.getVehicles().stream())
                    .map(BundleVehicle::getVin)
                    .distinct()
                    .toList();
            assertThat(allVins).contains("WBA00000000000001");
        }
    }

    @Nested
    class whenSomeVehiclesAreUnavailable {

        @Test
        void recommend_filtersOutUnavailableVehicles() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(
                    availableVehicle("WBA00000000000001", "BMW", "i3", 60000L, 460),
                    unavailableVehicle()
            ));
            given(calculateInstallmentPort.calculateInstallment(any(), any()))
                    .willReturn(Installment.of(Amount.ofCents(60000L, Currency.CHF)));

            FleetBundleRecommendation result = service.recommend(defaultRequirements(), Optional.empty());

            List<String> allVins = result.getBundles().stream()
                    .flatMap(b -> b.getVehicles().stream())
                    .map(BundleVehicle::getVin)
                    .toList();
            assertThat(allVins).doesNotContain("WBA99999999999999");
        }

        @Test
        void recommend_throwsWhenAllVehiclesUnavailable() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of(unavailableVehicle()));
            given(calculateInstallmentPort.calculateInstallment(any(), any()))
                    .willReturn(Installment.of(Amount.ofCents(70000L, Currency.CHF)));

            assertThatThrownBy(() -> service.recommend(defaultRequirements(), Optional.empty()))
                    .isInstanceOf(NoVehiclesMatchingRequirementsException.class);
        }
    }

    @Nested
    class whenNoCatalogueVehiclesExist {

        @Test
        void recommend_throwsWhenCatalogueIsEmpty() {
            given(getVehiclesPort.getVehicles()).willReturn(List.of());

            assertThatThrownBy(() -> service.recommend(defaultRequirements(), Optional.empty()))
                    .isInstanceOf(NoVehiclesMatchingRequirementsException.class);
        }
    }
}
