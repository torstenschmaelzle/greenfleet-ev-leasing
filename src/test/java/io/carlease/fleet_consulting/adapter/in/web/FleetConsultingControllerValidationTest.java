package io.carlease.fleet_consulting.adapter.in.web;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.domain.FleetBundleRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FleetConsultingController.class, FleetConsultingExceptionHandler.class})
class FleetConsultingControllerValidationTest {

    private static final String BASE_URL = "/api/fleet-consulting/recommendations"
            + "?daily_route_km=100"
            + "&number_of_vehicles=3"
            + "&lease_term_months=36"
            + "&electricity_rate_cents=28"
            + "&charging_type=COMPANY_CHARGER"
            + "&currency=CHF";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetRecommendationUseCase getRecommendationUseCase;

    @BeforeEach
    void setUp() {
        FleetBundleRecommendation emptyResult = mock(FleetBundleRecommendation.class, RETURNS_DEEP_STUBS);
        when(emptyResult.getBundles()).thenReturn(List.of());
        when(emptyResult.getRequestId()).thenReturn(UUID.randomUUID());
        when(emptyResult.getRequirements().getNumberOfVehicles().getValue()).thenReturn(3);
        when(emptyResult.getRequirements().getLeaseTerm().getMonths()).thenReturn(36);
        when(getRecommendationUseCase.recommend(any(), any())).thenReturn(emptyResult);
    }


    @Test
    void currency_valid_CHF_passes_validation() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().is(not(400)));
    }

    @Test
    void currency_lowercase_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL.replace("currency=CHF", "currency=chf")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "currency must be a 3-letter ISO 4217 code (e.g. CHF, EUR, USD)"));
    }

    @Test
    void currency_too_long_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL.replace("currency=CHF", "currency=CHFF")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "currency must be a 3-letter ISO 4217 code (e.g. CHF, EUR, USD)"));
    }



    @Test
    void chargingType_valid_PUBLIC_STATION_passes_validation() throws Exception {
        mockMvc.perform(get(BASE_URL.replace("charging_type=COMPANY_CHARGER", "charging_type=PUBLIC_STATION")))
                .andExpect(status().is(not(400)));
    }

    @Test
    void chargingType_unknown_value_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL.replace("charging_type=COMPANY_CHARGER", "charging_type=HOME")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "charging_type must be COMPANY_CHARGER, PUBLIC_STATION or MIXED"));
    }

    @Test
    void chargingType_lowercase_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL.replace("charging_type=COMPANY_CHARGER", "charging_type=company_charger")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "charging_type must be COMPANY_CHARGER, PUBLIC_STATION or MIXED"));
    }



    @Test
    void brand_valid_passes_validation() throws Exception {
        mockMvc.perform(get(BASE_URL + "&brand=Tesla"))
                .andExpect(status().is(not(400)));
    }

    @Test
    void brand_starts_with_digit_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL + "&brand=123Tesla"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "brand may only contain letters, digits, spaces and hyphens, and must start with a letter"));
    }

    @Test
    void brand_with_special_characters_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL + "&brand=Tesla@Motors"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "brand may only contain letters, digits, spaces and hyphens, and must start with a letter"));
    }

    @Test
    void vehicleType_valid_SUV_passes_validation() throws Exception {
        mockMvc.perform(get(BASE_URL + "&vehicle_type=SUV"))
                .andExpect(status().is(not(400)));
    }

    @Test
    void vehicleType_unknown_value_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL + "&vehicle_type=TRUCK"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "vehicle_type must be one of: SUV, SEDAN, VAN, COMPACT, ESTATE"));
    }

    @Test
    void vehicleType_lowercase_returns_400() throws Exception {
        mockMvc.perform(get(BASE_URL + "&vehicle_type=suv"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "vehicle_type must be one of: SUV, SEDAN, VAN, COMPACT, ESTATE"));
    }
}
