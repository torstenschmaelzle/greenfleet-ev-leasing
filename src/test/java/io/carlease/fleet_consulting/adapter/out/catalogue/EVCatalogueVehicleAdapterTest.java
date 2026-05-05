package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.fleet_consulting.domain.Kilometre;
import io.carlease.fleet_consulting.domain.VehicleSummary;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class EVCatalogueVehicleAdapterTest {

    private final ListCatalogueUseCase listCatalogueUseCase = mock(ListCatalogueUseCase.class);
    private final EVCatalogueVehicleAdapter adapter = new EVCatalogueVehicleAdapter(listCatalogueUseCase);

    // -- domain object helpers --

    private Vehicle availableVehicle(String brand, String model, VehicleType type,
                                      int rangeKm, BigDecimal kwhPer100km, long leasePriceCents) {
        return Vehicle.create(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of(brand, model, Range.ofKm(rangeKm), type,
                        EnergyConsumption.of(kwhPer100km)),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(leasePriceCents, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(rangeKm - 20)
        );
    }

    // -- tests --

    @Nested
    class whenCatalogueContainsVehicles {

        @Test
        void getVehicles_mapsVinFromVehicleId() {
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getVin()).isEqualTo("WBA12345678901234");
        }

        @Test
        void getVehicles_mapsBasePriceWithHealthDiscount() {
            // At 95% SOH: factor = 1 - max(0, 0.95 - 0.95) = 1.0 → no discount
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getBasePrice()).isEqualTo(Amount.ofCents(75000L, Currency.CHF));
        }

        @Test
        void getVehicles_mapsConsumptionToKwhPer100km() {
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getKwhPer100km()).isEqualTo(17.0);
        }

        @Test
        void getVehicles_mapsMeasuredRangeKmToKilometre() {
            // adapter uses latestMeasuredRangeKm (rangeKm - 20), not spec rangeKm
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getRangeKm()).isEqualTo(Kilometre.of(440));
        }

        @Test
        void getVehicles_mapsBrandModelAndType() {
            Vehicle vehicle = availableVehicle("Tesla", "Model Y", VehicleType.SUV, 530, new BigDecimal("15.0"), 90000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getBrand()).isEqualTo("Tesla");
            assertThat(summary.getModel()).isEqualTo("Model Y");
            assertThat(summary.getVehicleType()).isEqualTo(VehicleType.SUV);
        }

        @Test
        void getVehicles_setsAvailableTrueForAvailableVehicle() {
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.isAvailable()).isTrue();
        }

        @Test
        void getVehicles_setsAvailableFalseForRemovedVehicle() {
            Vehicle vehicle = availableVehicle("BMW", "iX3", VehicleType.SUV, 460, new BigDecimal("17.0"), 75000L);
            vehicle.remove();
            given(listCatalogueUseCase.listAll()).willReturn(List.of(vehicle));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.isAvailable()).isFalse();
        }

        @Test
        void getVehicles_returnsOneSummaryPerVehicle() {
            Vehicle bmw   = availableVehicle("BMW",   "iX3",     VehicleType.SUV,   460, new BigDecimal("17.0"), 75000L);
            Vehicle tesla = availableVehicle("Tesla",  "Model 3", VehicleType.SEDAN, 500, new BigDecimal("15.0"), 80000L);
            given(listCatalogueUseCase.listAll()).willReturn(List.of(bmw, tesla));

            List<VehicleSummary> summaries = adapter.getVehicles();

            assertThat(summaries).hasSize(2);
        }
    }

    @Nested
    class whenCatalogueIsEmpty {

        @Test
        void getVehicles_returnsEmptyList() {
            given(listCatalogueUseCase.listAll()).willReturn(List.of());

            assertThat(adapter.getVehicles()).isEmpty();
        }
    }
}
