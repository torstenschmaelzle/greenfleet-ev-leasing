package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.fleet_consulting.domain.VehicleSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("EVCatalogueVehicleAdapterIntegrationTest.sql")
class EVCatalogueVehicleAdapterIntegrationTest {

    @Autowired
    private EVCatalogueVehicleAdapter adapter;

    @Test
    void getVehicles_returnsAllVehiclesIncludingUnavailable() {
        List<VehicleSummary> summaries = adapter.getVehicles();

        assertThat(summaries).hasSize(2);
    }

    @Test
    void getVehicles_mapsAvailableVehicleCorrectly() {
        VehicleSummary bmw = adapter.getVehicles().stream()
                .filter(s -> s.getBrand().equals("BMW"))
                .findFirst().orElseThrow();

        assertThat(bmw.getModel()).isEqualTo("i3");
        assertThat(bmw.getVin()).isEqualTo("WBA00000000000301");
        assertThat(bmw.isAvailable()).isTrue();
    }

    @Test
    void getVehicles_setsAvailableFalseForRemovedVehicle() {
        VehicleSummary vw = adapter.getVehicles().stream()
                .filter(s -> s.getBrand().equals("VW"))
                .findFirst().orElseThrow();

        assertThat(vw.isAvailable()).isFalse();
    }
}
