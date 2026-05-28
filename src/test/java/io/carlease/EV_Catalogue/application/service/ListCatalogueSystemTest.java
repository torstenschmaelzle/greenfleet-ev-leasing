package io.carlease.ev_catalogue.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ListCatalogueSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql("ListCatalogueSystemTest.sql")
    void listAll_returnsSeededVehiclesAndRespondsWith200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/vehicles", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("WBA00000000000010", "WBA00000000000011");
    }
}
