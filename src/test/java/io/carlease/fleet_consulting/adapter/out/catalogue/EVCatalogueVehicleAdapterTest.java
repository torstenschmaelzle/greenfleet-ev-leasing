package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.CatalogueVehicleView;
import io.carlease.ev_catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.fleet_consulting.domain.Kilometre;
import io.carlease.fleet_consulting.domain.VehicleSummary;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class EVCatalogueVehicleAdapterTest {

    private final ListCatalogueUseCase listCatalogueUseCase = mock(ListCatalogueUseCase.class);
    private final EVCatalogueVehicleAdapter adapter = new EVCatalogueVehicleAdapter(listCatalogueUseCase);

    private CatalogueVehicleView view(String vin, String brand, String model, VehicleType type,
                                      double kwhPer100km, long leasePriceCents, int measuredRangeKm,
                                      boolean available) {
        return new CatalogueVehicleView(
                vin,
                Amount.ofCents(leasePriceCents, Currency.CHF),
                kwhPer100km,
                measuredRangeKm,
                brand, model, type,
                available
        );
    }

    @Nested
    class whenCatalogueContainsVehicles {

        @Test
        void getVehicles_mapsVin() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, true)));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getVin()).isEqualTo("WBA12345678901234");
        }

        @Test
        void getVehicles_mapsBasePrice() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, true)));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getBasePrice()).isEqualTo(Amount.ofCents(75000L, Currency.CHF));
        }

        @Test
        void getVehicles_mapsConsumption() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, true)));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getKwhPer100km()).isEqualTo(17.0);
        }

        @Test
        void getVehicles_mapsMeasuredRangeKm() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, true)));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getRangeKm()).isEqualTo(Kilometre.of(440));
        }

        @Test
        void getVehicles_mapsBrandModelAndType() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "Tesla", "Model Y", VehicleType.SUV, 15.0, 90000L, 510, true)));

            VehicleSummary summary = adapter.getVehicles().get(0);

            assertThat(summary.getBrand()).isEqualTo("Tesla");
            assertThat(summary.getModel()).isEqualTo("Model Y");
            assertThat(summary.getVehicleType()).isEqualTo(VehicleType.SUV);
        }

        @Test
        void getVehicles_mapsAvailableTrue() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, true)));

            assertThat(adapter.getVehicles().get(0).isAvailable()).isTrue();
        }

        @Test
        void getVehicles_mapsAvailableFalse() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW", "iX3", VehicleType.SUV, 17.0, 75000L, 440, false)));

            assertThat(adapter.getVehicles().get(0).isAvailable()).isFalse();
        }

        @Test
        void getVehicles_returnsOneSummaryPerVehicle() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of(
                    view("WBA12345678901234", "BMW",   "iX3",     VehicleType.SUV,   17.0, 75000L, 440, true),
                    view("WBA12345678901235", "Tesla", "Model 3", VehicleType.SEDAN, 15.0, 80000L, 480, true)));

            assertThat(adapter.getVehicles()).hasSize(2);
        }
    }

    @Nested
    class whenCatalogueIsEmpty {

        @Test
        void getVehicles_returnsEmptyList() {
            given(listCatalogueUseCase.listSummaries()).willReturn(List.of());

            assertThat(adapter.getVehicles()).isEmpty();
        }
    }
}
