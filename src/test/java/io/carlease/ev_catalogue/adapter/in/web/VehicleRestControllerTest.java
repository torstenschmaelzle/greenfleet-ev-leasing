package io.carlease.ev_catalogue.adapter.in.web;

import io.carlease.ev_catalogue.application.port.in.GetVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.ev_catalogue.application.port.in.AddVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.RemoveVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.UpdateVehicleUseCase;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehicleRestController.class)
class VehicleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListCatalogueUseCase listCatalogueUseCase;

    @MockBean
    private GetVehicleUseCase getVehicleUseCase;

    @MockBean
    private AddVehicleUseCase addVehicleUseCase;

    @MockBean
    private UpdateVehicleUseCase updateVehicleUseCase;

    @MockBean
    private RemoveVehicleUseCase removeVehicleUseCase;

    private static final String VALID_VIN = "WBA12345678901234";

    private Vehicle sampleVehicle() {
        return VehicleFactory.createVehicle(
                VehicleId.of(VALID_VIN),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
    }

    private static final String ADD_REQUEST_BODY = """
            {
              "vin": "WBA12345678901234",
              "brand": "BMW",
              "model": "iX3",
              "rangeKm": 460,
              "vehicleType": "SUV",
              "consumptionKwhPer100km": 17.0,
              "capacityKwh": 80.0,
              "voltageV": 400.0,
              "maxChargeSpeedKw": 11.0,
              "horsePowerHp": 286,
              "accelerationSec": 6.8,
              "topSpeedKmh": 180,
              "monthlyLeasePriceCents": 75000,
              "currency": "CHF",
              "initialStateOfHealth": 95.0,
              "initialMeasuredRangeKm": 440
            }
            """;

    private static final String UPDATE_REQUEST_BODY = """
            {
              "brand": "BMW",
              "model": "iX3",
              "rangeKm": 460,
              "vehicleType": "SUV",
              "consumptionKwhPer100km": 17.0,
              "capacityKwh": 80.0,
              "voltageV": 400.0,
              "maxChargeSpeedKw": 11.0,
              "monthlyLeasePriceCents": 80000,
              "currency": "CHF",
              "newStateOfHealth": 90.0,
              "newMeasuredRangeKm": 420
            }
            """;

    @Nested
    class listEndpoint {

        @Test
        void returnsAllVehiclesAsJson() throws Exception {
            given(listCatalogueUseCase.listAll()).willReturn(List.of(sampleVehicle()));

            mockMvc.perform(get("/api/vehicles"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].vin").value(VALID_VIN))
                    .andExpect(jsonPath("$[0].brand").value("BMW"))
                    .andExpect(jsonPath("$[0].model").value("iX3"))
                    .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
        }

        @Test
        void returnsEmptyArrayWhenCatalogueIsEmpty() throws Exception {
            given(listCatalogueUseCase.listAll()).willReturn(List.of());

            mockMvc.perform(get("/api/vehicles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class getEndpoint {

        @Test
        void returns200WithVehicleDtoWhenFound() throws Exception {
            given(getVehicleUseCase.getByVin(VehicleId.of(VALID_VIN)))
                    .willReturn(sampleVehicle());

            mockMvc.perform(get("/api/vehicles/{vin}", VALID_VIN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vin").value(VALID_VIN))
                    .andExpect(jsonPath("$.brand").value("BMW"))
                    .andExpect(jsonPath("$.monthlyLeasePriceCents").value(75000));
        }

        @Test
        void returns404WhenVehicleNotFound() throws Exception {
            willThrow(new VehicleNotFoundException(VehicleId.of(VALID_VIN)))
                    .given(getVehicleUseCase).getByVin(VehicleId.of(VALID_VIN));

            mockMvc.perform(get("/api/vehicles/{vin}", VALID_VIN))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class addEndpoint {

        @Test
        void returns201AndInvokesUseCaseWithCorrectDomainObjects() throws Exception {
            willDoNothing().given(addVehicleUseCase).add(
                    VehicleId.of(VALID_VIN),
                    VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                            EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                    BatterySpecification.of(BigDecimal.valueOf(80.0), BigDecimal.valueOf(400.0), BigDecimal.valueOf(11.0)),
                    Performance.of(286, BigDecimal.valueOf(6.8), 180),
                    Amount.ofCents(75000L, Currency.CHF),
                    Percentage.of(BigDecimal.valueOf(95.0)),
                    Range.ofKm(440)
            );

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ADD_REQUEST_BODY))
                    .andExpect(status().isCreated());

            then(addVehicleUseCase).should().add(
                    VehicleId.of(VALID_VIN),
                    VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                            EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                    BatterySpecification.of(BigDecimal.valueOf(80.0), BigDecimal.valueOf(400.0), BigDecimal.valueOf(11.0)),
                    Performance.of(286, BigDecimal.valueOf(6.8), 180),
                    Amount.ofCents(75000L, Currency.CHF),
                    Percentage.of(BigDecimal.valueOf(95.0)),
                    Range.ofKm(440)
            );
        }

        @Test
        void returns409WhenVehicleVinAlreadyExists() throws Exception {
            willThrow(new DuplicateVehicleVinException(VALID_VIN))
                    .given(addVehicleUseCase).add(
                            VehicleId.of(VALID_VIN),
                            VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                                    EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                            BatterySpecification.of(BigDecimal.valueOf(80.0), BigDecimal.valueOf(400.0), BigDecimal.valueOf(11.0)),
                            Performance.of(286, BigDecimal.valueOf(6.8), 180),
                            Amount.ofCents(75000L, Currency.CHF),
                            Percentage.of(BigDecimal.valueOf(95.0)),
                            Range.ofKm(440)
                    );

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ADD_REQUEST_BODY))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class updateEndpoint {

        @Test
        void returns200AndInvokesUseCaseWithCorrectDomainObjects() throws Exception {
            willDoNothing().given(updateVehicleUseCase).update(
                    VehicleId.of(VALID_VIN),
                    VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                            EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                    BatterySpecification.of(BigDecimal.valueOf(80.0), BigDecimal.valueOf(400.0), BigDecimal.valueOf(11.0)),
                    Amount.ofCents(80000L, Currency.CHF),
                    Percentage.of(BigDecimal.valueOf(90.0)),
                    Range.ofKm(420)
            );

            mockMvc.perform(put("/api/vehicles/{vin}", VALID_VIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(UPDATE_REQUEST_BODY))
                    .andExpect(status().isOk());

            then(updateVehicleUseCase).should().update(
                    VehicleId.of(VALID_VIN),
                    VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                            EnergyConsumption.of(BigDecimal.valueOf(17.0))),
                    BatterySpecification.of(BigDecimal.valueOf(80.0), BigDecimal.valueOf(400.0), BigDecimal.valueOf(11.0)),
                    Amount.ofCents(80000L, Currency.CHF),
                    Percentage.of(BigDecimal.valueOf(90.0)),
                    Range.ofKm(420)
            );
        }

        @Test
        void returns400WhenVehicleAttributeIsInvalid() throws Exception {
            willThrow(new InvalidVehicleAttributeException("rangeKm", "must be positive"))
                    .given(updateVehicleUseCase).update(any(), any(), any(), any(), any(), any());

            mockMvc.perform(put("/api/vehicles/{vin}", VALID_VIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(UPDATE_REQUEST_BODY))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    class removeEndpoint {

        @Test
        void returns204AndInvokesUseCase() throws Exception {
            willDoNothing().given(removeVehicleUseCase).remove(VehicleId.of(VALID_VIN));

            mockMvc.perform(delete("/api/vehicles/{vin}", VALID_VIN))
                    .andExpect(status().isNoContent());

            then(removeVehicleUseCase).should().remove(VehicleId.of(VALID_VIN));
        }

        @Test
        void returns404WhenVehicleNotFound() throws Exception {
            willThrow(new VehicleNotFoundException(VehicleId.of(VALID_VIN)))
                    .given(removeVehicleUseCase).remove(VehicleId.of(VALID_VIN));

            mockMvc.perform(delete("/api/vehicles/{vin}", VALID_VIN))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }
}
