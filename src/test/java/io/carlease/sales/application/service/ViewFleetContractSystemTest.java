package io.carlease.sales.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ViewFleetContractSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql("ViewFleetContractSystemTest.sql")
    void viewFleetContract_returnsFleetContractWithVehicles() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/sales/fleet-contracts/fc-view-test-001", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("fc-view-test-001")
                .contains("John Buyer")
                .contains("ACME AG")
                .contains("ECO_FOCUSED")
                .contains("WBA00000000000111")
                .contains("WBA00000000000112");
    }
}
