package io.carlease.sales.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FleetContractSteps {

    private static final String VIN_RESERVED  = "WBA00000000000301";
    private static final String VIN_AVAILABLE = "WBA00000000000302";

    @Autowired
    private TestRestTemplate restTemplate;

    // JdbcTemplate is used ONLY for test setup/teardown.
    // No query endpoint exists for deleting test data, so direct DB access is the only option here.
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String contractNumber;
    private ResponseEntity<Object> lastSignResponse;

    @Before
    public void cleanUp() {
        jdbcTemplate.update("""
                DELETE FROM fleet_contract_vehicle_numbers
                WHERE fleet_contract_number IN (
                    SELECT number FROM fleet_contracts WHERE customer_email = 'cucumber@test.com'
                )
                """);
        jdbcTemplate.update("DELETE FROM contracts WHERE lessee_email = 'cucumber@test.com'");
        jdbcTemplate.update("DELETE FROM fleet_contracts WHERE customer_email = 'cucumber@test.com'");
        jdbcTemplate.update("DELETE FROM battery_health_records WHERE vin IN (?, ?)", VIN_RESERVED, VIN_AVAILABLE);
        jdbcTemplate.update("DELETE FROM vehicles WHERE vin IN (?, ?)", VIN_RESERVED, VIN_AVAILABLE);
    }

    @After
    public void tearDown() {
        cleanUp();
    }

    // --- Given ---

    @Given("a vehicle {string} is reserved in the catalogue")
    public void aVehicleIsReservedInTheCatalogue(String vin) {
        insertVehicle(vin, "RESERVED");
    }

    @Given("a vehicle {string} is available in the catalogue")
    public void aVehicleIsAvailableInTheCatalogue(String vin) {
        insertVehicle(vin, "AVAILABLE");
    }

    // --- When ---

    @When("the customer creates a fleet contract for vehicle {string}")
    public void theCustomerCreatesAFleetContractForVehicle(String vin) {
        String body = """
                {
                  "customerName": "Cucumber Tester",
                  "customerCompany": "Test AG",
                  "customerEmail": "cucumber@test.com",
                  "leaseTermMonths": 36,
                  "bundleType": "ECO_FOCUSED",
                  "vehicles": [
                    { "vin": "%s", "monthlyInstallmentCents": 75000, "currency": "CHF" }
                  ]
                }
                """.formatted(vin);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sales/fleet-contracts",
                new HttpEntity<>(body, jsonHeaders()),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        contractNumber = response.getBody().replace("\"", "");
    }

    @When("the customer signs the contract")
    public void theCustomerSignsTheContract() {
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/sales/fleet-contracts/" + contractNumber + "/sign",
                null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @And("the customer tries to sign the contract")
    public void theCustomerTriesToSignTheContract() {
        lastSignResponse = restTemplate.postForEntity(
                "/api/sales/fleet-contracts/" + contractNumber + "/sign",
                null, Object.class);
    }

    // --- Then ---

    @Then("a valid contract number is persisted in the system")
    public void aValidContractNumberIsPersistedInTheSystem() {
        assertThat(contractNumber).isNotBlank();
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/sales/fleet-contracts/" + contractNumber, Map.class);
        assertThat(response.getStatusCode())
                .as("Contract should be retrievable via GET after creation")
                .isEqualTo(HttpStatus.OK);
    }

    @And("the vehicle {string} is still reserved")
    public void theVehicleIsStillReserved(String vin) {
        assertThat(vehicleStatusViaApi(vin))
                .as("Vehicle should remain RESERVED after contract creation — create must not touch vehicle state")
                .isEqualTo("RESERVED");
    }

    @Then("the contract is marked as signed")
    public void theContractIsMarkedAsSigned() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/sales/fleet-contracts/" + contractNumber, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("signed"))
                .as("Contract should be marked as signed after signing")
                .isEqualTo(true);
    }

    @And("the vehicle {string} is under contract")
    public void theVehicleIsUnderContract(String vin) {
        assertThat(vehicleStatusViaApi(vin))
                .as("Vehicle should be UNDER_CONTRACT after signing")
                .isEqualTo("UNDER_CONTRACT");
    }

    @Then("the signing is rejected with an error")
    public void theSigningIsRejectedWithAnError() {
        assertThat(lastSignResponse.getStatusCode())
                .as("Expected 409 CONFLICT — EVCatalogueExceptionHandler maps VehicleContractViolationException to 409")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @And("the contract is not yet signed")
    public void theContractIsNotYetSigned() {
        // This step exposes the missing @Transactional on SignFleetContractService.sign().
        // Because there is no transaction wrapping the method, fleetContractPersistencePort.save()
        // commits the signature_date BEFORE the vehicle state transition throws — leaving the DB
        // in an inconsistent state. This assertion is expected to FAIL until @Transactional is added.
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/sales/fleet-contracts/" + contractNumber, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("signed"))
                .as("Contract must NOT be signed when the signing attempt failed — requires @Transactional on SignFleetContractService.sign()")
                .isEqualTo(false);
    }

    @And("the vehicle {string} is still available")
    public void theVehicleIsStillAvailable(String vin) {
        assertThat(vehicleStatusViaApi(vin))
                .as("Vehicle should remain AVAILABLE after a failed signing attempt")
                .isEqualTo("AVAILABLE");
    }

    // --- Helpers ---

    @SuppressWarnings("unchecked")
    private String vehicleStatusViaApi(String vin) {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/vehicles/" + vin, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return (String) response.getBody().get("status");
    }

    private void insertVehicle(String vin, String status) {
        jdbcTemplate.update("""
                INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type,
                    consumption_kwh_per100km, battery_capacity_kwh, battery_voltagev,
                    battery_max_charge_speed_kw, horse_power_hp, acceleration_sec,
                    top_speed_kmh, monthly_lease_price_cents, monthly_lease_price_currency,
                    status, lock_id, locked_at, expires_at, lock_duration_minutes)
                VALUES (?, 'BMW', 'iX3', 460, 'SUV', 17.0, 80.0, 400.0, 11.0,
                    286, 6.8, 180, 75000, 'CHF', ?,
                    '00000000-0000-0000-0000-000000000301',
                    CURRENT_TIMESTAMP, TIMESTAMPADD(HOUR, 8, CURRENT_TIMESTAMP), 480)
                """, vin, status);
        jdbcTemplate.update("""
                INSERT INTO battery_health_records
                    (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
                VALUES (?, 0, RANDOM_UUID(), CURRENT_TIMESTAMP, 95.0, 440)
                """, vin);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
