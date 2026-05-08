package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.ListCataloguePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ListCatalogueServiceTest {

    private final ListCataloguePort listCataloguePort = mock(ListCataloguePort.class);
    private final ListCatalogueService service = new ListCatalogueService(listCataloguePort);

    @Test
    void returnsAllVehiclesFromPort() {
        List<Vehicle> catalogue = List.of(
                VehicleFactory.createVehicle(
                        VehicleId.of("WBA12345678654231"),
                        VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                                EnergyConsumption.of(new BigDecimal("17"))),
                        BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                        Performance.of(286, new BigDecimal("6.8"), 180),
                        Amount.ofCents(75000L, Currency.CHF),
                        Percentage.of(new BigDecimal("95")),
                        Range.ofKm(440)
                ),
                VehicleFactory.createVehicle(
                        VehicleId.of("WBA12345678654232"),
                        VehicleSpecification.of("Tesla", "Model 3", Range.ofKm(500), VehicleType.SEDAN,
                                EnergyConsumption.of(new BigDecimal("15"))),
                        BatterySpecification.of(new BigDecimal("82"), new BigDecimal("400"), new BigDecimal("11")),
                        Performance.of(283, new BigDecimal("5.6"), 225),
                        Amount.ofCents(80000L, Currency.CHF),
                        Percentage.of(new BigDecimal("97")),
                        Range.ofKm(480)
                )
        );
        given(listCataloguePort.listAll()).willReturn(catalogue);

        List<Vehicle> result = service.listAll();

        assertThat(result).isEqualTo(catalogue);
        then(listCataloguePort).should().listAll();
    }

    @Test
    void returnsEmptyListWhenCatalogueIsEmpty() {
        given(listCataloguePort.listAll()).willReturn(List.of());

        List<Vehicle> result = service.listAll();

        assertThat(result).isEmpty();
        then(listCataloguePort).should().listAll();
    }
}
