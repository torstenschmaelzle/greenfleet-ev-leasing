package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.VehicleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveVehicleSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehiclePersistencePort vehiclePersistencePort;

    @Test
    @Sql("RemoveVehicleSystemTest.sql")
    void removeVehicle_deletesVehicleAndReturns204() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/vehicles/WBA00000000000030",
                org.springframework.http.HttpMethod.DELETE,
                null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(vehiclePersistencePort.existsById(VehicleId.of("WBA00000000000030"))).isFalse();
    }
}
