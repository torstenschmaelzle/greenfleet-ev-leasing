package io.carlease.fleet_consulting.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GetRecommendationSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql("GetRecommendationSystemTest.sql")
    void getRecommendations_returnsAvailableVehiclesInBundlesAndExcludesRemovedOnes() {
        // 2 vehicles fleet, 36-month lease, BMW i3 (AVAILABLE) and Tesla Model 3 (AVAILABLE) should appear in bundles,
        // VW ID.4 has status REMOVED and must not appear anywhere in the response
        URI uri = UriComponentsBuilder.fromPath("/api/fleet-consulting/recommendations")
                .queryParam("daily_route_km",          100)
                .queryParam("number_of_vehicles",       2)
                .queryParam("lease_term_months",        36)
                .queryParam("charging_type",            "COMPANY_CHARGER")
                .queryParam("electricity_rate_cents",   30)
                .queryParam("currency",                 "CHF")
                .queryParam("budget_monthly_max_cents", 200000)
                .build().encode().toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("BMW", "Tesla");
        assertThat(response.getBody()).doesNotContain("VW");
    }
}
