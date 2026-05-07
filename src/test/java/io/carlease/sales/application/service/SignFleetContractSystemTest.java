package io.carlease.sales.application.service;

import io.carlease.sales.adapter.out.persistence.FleetContractJpaEntity;
import io.carlease.sales.adapter.out.persistence.FleetContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SignFleetContractSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FleetContractRepository fleetContractRepository;

    @Test
    @Sql("SignFleetContractSystemTest.sql")
    void signFleetContract_persistsSignatureDateOnFleetAndVehicleContracts() {
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/sales/fleet-contracts/fc-sign-test-001/sign", null, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        FleetContractJpaEntity fleet = fleetContractRepository
                .findById("fc-sign-test-001").orElseThrow();
        assertThat(fleet.getSignatureDate()).isNotNull();
    }
}
