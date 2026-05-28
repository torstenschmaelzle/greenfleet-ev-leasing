package io.carlease.sales.application.service;

import io.carlease.sales.adapter.out.persistence.ContractRepository;
import io.carlease.sales.adapter.out.persistence.FleetContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateFleetContractSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FleetContractRepository fleetContractRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void createFleetContract_persistsFleetAndVehicleContractsAndReturnsContractNumber() {
        String body = """
                {
                  "customerName": "John Buyer",
                  "customerCompany": "ACME AG",
                  "customerEmail": "john@example.com",
                  "leaseTermMonths": 36,
                  "bundleType": "BALANCED",
                  "vehicles": [
                    { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "CHF" },
                    { "vin": "WBA00000000000102", "monthlyInstallmentCents": 45000, "currency": "CHF" }
                  ]
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sales/fleet-contracts",
                new HttpEntity<>(body, headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String contractNumber = response.getBody().replace("\"", "");
        assertThat(contractNumber).isNotBlank();
        assertThat(fleetContractRepository.existsById(contractNumber)).isTrue();
    }
}
