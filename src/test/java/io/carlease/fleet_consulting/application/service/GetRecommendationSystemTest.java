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
    void getRecommendations_returnsAvailableVehiclesRankedByScoreAndExcludesRemovedOnes() {
        // daily_route_km=100, 2 drivers, COMPANY_CHARGER at 0.30 CHF/kWh, budget=2000 CHF/month
        // BMW i3 (600 CHF lease) scores higher than Tesla Model 3 (1500 CHF lease) under default weights
        // VW ID.4 has status REMOVED and must be filtered out
        URI uri = UriComponentsBuilder.fromPath("/api/fleet-consulting/recommendations")
                .queryParam("daily_route_km",          100)
                .queryParam("number_of_drivers",        2)
                .queryParam("charging_type",            "COMPANY_CHARGER")
                .queryParam("electricity_rate_cents",   30)
                .queryParam("currency",                 "CHF")
                .queryParam("budget_monthly_max_cents", 200000)
                .build().encode().toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("BMW", "Tesla");
        assertThat(response.getBody()).doesNotContain("VW");
        assertThat(response.getBody().indexOf("BMW")).isLessThan(response.getBody().indexOf("Tesla"));
    }
}
