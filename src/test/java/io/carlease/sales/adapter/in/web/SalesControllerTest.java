package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.ApproveContractUseCase;
import io.carlease.sales.application.port.in.CalculateInstallmentUseCase;
import io.carlease.sales.application.port.in.FilloutContractUseCase;
import io.carlease.sales.application.port.in.ViewContractUseCase;
import io.carlease.sales.domain.Amount;
import io.carlease.sales.domain.Car;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.Currency;
import io.carlease.sales.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import io.carlease.sales.application.port.in.SignContractUseCase;

@WebMvcTest(controllers = SalesController.class)
public class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilloutContractUseCase filloutContractUseCase;

    @MockBean
    private ViewContractUseCase viewContractUseCase;

    @MockBean
    private ApproveContractUseCase approveContractUseCase;

    @MockBean
    private SignContractUseCase signContractUseCase;
    private CalculateInstallmentUseCase calculateInstallmentUseCase;

    @Test
    void testFilloutContract() throws Exception {

        willDoNothing().given(filloutContractUseCase).with(
                ContractNumber.of("1"),
                Customer.of("John Buyer"),
                Car.of("Volkswagen ID.3"),
                Amount.of(50000, Currency.valueOf("EUR"))
        );

        mockMvc.perform(post("/sales/fillout_contract")
                        .param("number", "1")
                        .param("lessee", "John Buyer")
                        .param("car", "Volkswagen ID.3")
                        .param("price_amount", "50000")
                        .param("price_currency", "EUR"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/sales/view_contract?number=1"));

        then(filloutContractUseCase).should()
                .with(
                        ContractNumber.of("1"),
                        Customer.of("John Buyer"),
                        Car.of("Volkswagen ID.3"),
                        Amount.of(50000, Currency.valueOf("EUR"))
                );
    }

    @Test
    void testSignContract() throws Exception {

        // arrange (given)
        willDoNothing().given(signContractUseCase).with(
                eq(ContractNumber.of("1")),
                any(SignDate.class)
        );

        // act (when)
        mockMvc.perform(post("/sales/sign_contract")
                        .param("number", "1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/sales/view_contract?number=1"));

        // assert (then)
        then(signContractUseCase).should()
                .with(eq(ContractNumber.of("1")), any(SignDate.class));
    }

}
    void testCalculateInstallment() throws Exception {

        willDoNothing().given(calculateInstallmentUseCase).calculateInstallment(
                "1",
                36,
                5.0,
                1000.0,
                5000.0
        );

        mockMvc.perform(post("/sales/calculate_installment")
                        .param("number", "1")
                        .param("lease_term", "36")
                        .param("interest", "5.0")
                        .param("down_payment", "1000.0")
                        .param("residual_value", "5000.0"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/sales/view_contract?number=1"));

        then(calculateInstallmentUseCase).should()
                .calculateInstallment("1", 36, 5.0, 1000.0, 5000.0);
    }
}
