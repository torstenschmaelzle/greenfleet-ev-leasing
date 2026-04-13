package io.carlease.EV_Catalogue.adapter.in.web;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.in.removeVehicle;
import io.carlease.EV_Catalogue.application.port.in.updateVehicle;
import io.carlease.EV_Catalogue.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EVCatalogueController.class)
class EVCatalogueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private addVehicle addVehicleUseCase;

    @MockBean
    private updateVehicle updateVehicleUseCase;

    @MockBean
    private removeVehicle removeVehicleUseCase;

    @MockBean
    private ListCatalogueUseCase listCatalogueUseCase;

    @Test
    void addVehicle_redirectsToListAndInvokesUseCaseWithCorrectDomainObjects() throws Exception {
        willDoNothing().given(addVehicleUseCase).add(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                BatterySpecification.of(
                        BigDecimal.valueOf(80.0),
                        BigDecimal.valueOf(400.0),
                        BigDecimal.valueOf(11.0)),
                Performance.of(286, BigDecimal.valueOf(6.8), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(BigDecimal.valueOf(95.0)),
                Range.ofKm(440)
        );

        mockMvc.perform(post("/ev-catalogue/add_vehicle")
                        .param("vin",                          "WBA12345678901234")
                        .param("brand",                        "BMW")
                        .param("model",                        "iX3")
                        .param("range_km",                     "460")
                        .param("vehicle_type",                 "SUV")
                        .param("consumption_kwh_per_100km",    "17.0")
                        .param("battery_capacity_kwh",         "80.0")
                        .param("battery_voltage_v",            "400.0")
                        .param("battery_max_charge_speed_kw",  "11.0")
                        .param("horse_power_hp",               "286")
                        .param("acceleration_sec",             "6.8")
                        .param("top_speed_kmh",                "180")
                        .param("monthly_lease_price_cents",    "75000")
                        .param("currency",                     "CHF")
                        .param("initial_state_of_health",      "95.0")
                        .param("initial_measured_range_km",    "440"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/ev-catalogue/list"));

        then(addVehicleUseCase).should().add(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                BatterySpecification.of(
                        BigDecimal.valueOf(80.0),
                        BigDecimal.valueOf(400.0),
                        BigDecimal.valueOf(11.0)),
                Performance.of(286, BigDecimal.valueOf(6.8), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(BigDecimal.valueOf(95.0)),
                Range.ofKm(440)
        );
    }

    @Test
    void updateVehicle_redirectsToListAndInvokesUseCaseWithCorrectDomainObjects() throws Exception {
        willDoNothing().given(updateVehicleUseCase).update(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                BatterySpecification.of(
                        BigDecimal.valueOf(80.0),
                        BigDecimal.valueOf(400.0),
                        BigDecimal.valueOf(11.0)),
                Amount.ofCents(80000L, Currency.CHF),
                Percentage.of(BigDecimal.valueOf(90.0)),
                Range.ofKm(420)
        );

        mockMvc.perform(post("/ev-catalogue/update_vehicle")
                        .param("vin",                          "WBA12345678901234")
                        .param("brand",                        "BMW")
                        .param("model",                        "iX3")
                        .param("range_km",                     "460")
                        .param("vehicle_type",                 "SUV")
                        .param("consumption_kwh_per_100km",    "17.0")
                        .param("battery_capacity_kwh",         "80.0")
                        .param("battery_voltage_v",            "400.0")
                        .param("battery_max_charge_speed_kw",  "11.0")
                        .param("monthly_lease_price_cents",    "80000")
                        .param("currency",                     "CHF")
                        .param("new_state_of_health",          "90.0")
                        .param("new_measured_range_km",        "420"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/ev-catalogue/list"));

        then(updateVehicleUseCase).should().update(
                VehicleId.of("WBA12345678901234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                BatterySpecification.of(
                        BigDecimal.valueOf(80.0),
                        BigDecimal.valueOf(400.0),
                        BigDecimal.valueOf(11.0)),
                Amount.ofCents(80000L, Currency.CHF),
                Percentage.of(BigDecimal.valueOf(90.0)),
                Range.ofKm(420)
        );
    }

    @Test
    void removeVehicle_redirectsToListAndInvokesUseCaseWithCorrectDomainObject() throws Exception {
        willDoNothing().given(removeVehicleUseCase).remove(VehicleId.of("WBA12345678901234"));

        mockMvc.perform(post("/ev-catalogue/remove_vehicle")
                        .param("vin", "WBA12345678901234"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/ev-catalogue/list"));

        then(removeVehicleUseCase).should().remove(VehicleId.of("WBA12345678901234"));
    }

    @Test
    void listCatalogue_invokesUseCase() throws Exception {
        given(listCatalogueUseCase.listAll()).willReturn(List.of());

        mockMvc.perform(get("/ev-catalogue/list"))
                .andExpect(status().isOk());

        then(listCatalogueUseCase).should().listAll();
    }
}
