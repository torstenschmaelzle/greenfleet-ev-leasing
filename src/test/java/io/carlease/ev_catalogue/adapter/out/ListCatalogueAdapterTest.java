package io.carlease.ev_catalogue.adapter.out;

import io.carlease.ev_catalogue.domain.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ListCatalogueAdapter.class)
class ListCatalogueAdapterTest {

    @Autowired
    private ListCatalogueAdapter adapter;

    @Test
    @Sql("ListCatalogueAdapterTest.sql")
    void listsAllVehicles() {
        List<Vehicle> vehicles = adapter.listAll();

        assertThat(vehicles).hasSize(2);
        assertThat(vehicles)
                .extracting(v -> v.getVehicleId().getVin())
                .containsExactlyInAnyOrder("WBA00000000000020", "WBA00000000000021");
    }

    @Test
    void returnsEmptyListWhenNoVehiclesExist() {
        List<Vehicle> vehicles = adapter.listAll();

        assertThat(vehicles).isEmpty();
    }
}
