package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.*;
import io.carlease.sales.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FleetContractController.class)
class FleetContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateFleetContractUseCase createUseCase;
    @MockBean private ListFleetContractsUseCase listUseCase;
    @MockBean private ViewFleetContractUseCase viewUseCase;
    @MockBean private SignFleetContractUseCase signUseCase;
    @MockBean private ReserveVehiclesUseCase reserveUseCase;
    @MockBean private ReleaseVehiclesUseCase releaseUseCase;

    private static final String VALID_CREATE_BODY = """
        {
          "customerName": "John Buyer",
          "customerCompany": "ACME AG",
          "customerEmail": "john@example.com",
          "leaseTermMonths": 36,
          "bundleType": "ECO_FOCUSED",
          "vehicles": [
            { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "CHF" }
          ]
        }
        """;

    private static final String VALID_VINS_BODY = """
        { "vins": ["WBA00000000000101", "WBA00000000000102"] }
        """;

    private FleetContract aFleetContract(String number) {
        var vc = ContractFactory.restoreFleetVehicleContract(
            ContractNumber.of("VC-001"),
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            Vehicle.of("WBA00000000000101"),
            Amount.ofCents(300000L, Currency.CHF),
            Amount.ofCents(60000L, Currency.CHF),
            Optional.empty()
        );
        return ContractFactory.restoreFleetContract(
            ContractNumber.of(number),
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            LeaseTerm.of(36),
            "ECO_FOCUSED",
            List.of(vc),
            Optional.empty()
        );
    }

    @Nested
    class createEndpoint {

        @Test
        void validRequest_returns200() throws Exception {
            given(createUseCase.create(any(), any(), any(), anyInt(), any(), any()))
                .willReturn(ContractNumber.of("FC-12345"));

            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_CREATE_BODY))
                .andExpect(status().isOk());

            then(createUseCase).should().create(any(), any(), any(), anyInt(), any(), any());
        }

        // --- Bean Validation (one test per distinct constraint type) ---

        @Test
        void blankCustomerName_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "customerName": "",
                          "customerCompany": "ACME AG",
                          "customerEmail": "john@example.com",
                          "leaseTermMonths": 36,
                          "bundleType": "ECO_FOCUSED",
                          "vehicles": [
                            { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "CHF" }
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        void invalidEmailFormat_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "customerName": "John Buyer",
                          "customerCompany": "ACME AG",
                          "customerEmail": "not-an-email",
                          "leaseTermMonths": 36,
                          "bundleType": "ECO_FOCUSED",
                          "vehicles": [
                            { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "CHF" }
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        void zeroLeaseTermMonths_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "customerName": "John Buyer",
                          "customerCompany": "ACME AG",
                          "customerEmail": "john@example.com",
                          "leaseTermMonths": 0,
                          "bundleType": "ECO_FOCUSED",
                          "vehicles": [
                            { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "CHF" }
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        void emptyVehiclesList_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "customerName": "John Buyer",
                          "customerCompany": "ACME AG",
                          "customerEmail": "john@example.com",
                          "leaseTermMonths": 36,
                          "bundleType": "ECO_FOCUSED",
                          "vehicles": []
                        }
                        """))
                .andExpect(status().isBadRequest());
        }

        @Test
        void lowercaseCurrencyInVehicleEntry_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "customerName": "John Buyer",
                          "customerCompany": "ACME AG",
                          "customerEmail": "john@example.com",
                          "leaseTermMonths": 36,
                          "bundleType": "ECO_FOCUSED",
                          "vehicles": [
                            { "vin": "WBA00000000000101", "monthlyInstallmentCents": 60000, "currency": "chf" }
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest());
        }

        // --- Domain exceptions → 400 (tests SalesExceptionHandler wiring) ---

        @Test
        void invalidCustomerAttribute_returns400() throws Exception {
            willThrow(new InvalidCustomerAttributeException("customerName", "invalid format"))
                .given(createUseCase).create(any(), any(), any(), anyInt(), any(), any());

            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_CREATE_BODY))
                .andExpect(status().isBadRequest());
        }

        @Test
        void invalidFleetAttribute_returns400() throws Exception {
            willThrow(new InvalidFleetAttributeException("vehicleContracts", "fleet must contain at least one vehicle"))
                .given(createUseCase).create(any(), any(), any(), anyInt(), any(), any());

            mockMvc.perform(post("/api/sales/fleet-contracts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_CREATE_BODY))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class getEndpoint {

        @Test
        void existingContract_returns200WithBody() throws Exception {
            given(viewUseCase.with(ContractNumber.of("FC-001")))
                .willReturn(aFleetContract("FC-001"));

            mockMvc.perform(get("/api/sales/fleet-contracts/FC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("FC-001"));
        }

        @Test
        void unknownContract_returns404() throws Exception {
            given(viewUseCase.with(ContractNumber.of("UNKNOWN")))
                .willThrow(new ContractNotFoundException("UNKNOWN"));

            mockMvc.perform(get("/api/sales/fleet-contracts/UNKNOWN"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class signEndpoint {

        @Test
        void validContract_returns200() throws Exception {
            willDoNothing().given(signUseCase).sign(any(), any());

            mockMvc.perform(post("/api/sales/fleet-contracts/FC-001/sign"))
                .andExpect(status().isOk());

            then(signUseCase).should().sign(any(), any());
        }

        @Test
        void alreadySignedContract_returns409() throws Exception {
            willThrow(new ContractAlreadySignedException("FC-001"))
                .given(signUseCase).sign(any(), any());

            mockMvc.perform(post("/api/sales/fleet-contracts/FC-001/sign"))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    class reserveEndpoint {

        @Test
        void validRequest_returns200() throws Exception {
            willDoNothing().given(reserveUseCase).reserve(any());

            mockMvc.perform(post("/api/sales/fleet-contracts/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_VINS_BODY))
                .andExpect(status().isOk());
        }

        @Test
        void emptyVinsList_returns400() throws Exception {
            mockMvc.perform(post("/api/sales/fleet-contracts/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        { "vins": [] }
                        """))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class releaseEndpoint {

        @Test
        void validRequest_returns200() throws Exception {
            willDoNothing().given(releaseUseCase).release(any());

            mockMvc.perform(delete("/api/sales/fleet-contracts/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_VINS_BODY))
                .andExpect(status().isOk());
        }
    }
}
