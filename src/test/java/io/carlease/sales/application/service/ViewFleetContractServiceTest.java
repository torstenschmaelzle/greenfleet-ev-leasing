package io.carlease.sales.application.service;

import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.*;
import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ViewFleetContractServiceTest {

    private final FleetContractPersistencePort persistencePort = mock(FleetContractPersistencePort.class);
    private final ViewFleetContractService service = new ViewFleetContractService(persistencePort);

    private FleetContract aFleetContract(ContractNumber number) {
        return ContractFactory.restoreFleetContract(
            number,
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            LeaseTerm.of(36),
            BundleType.BALANCED,
            List.of(
                ContractFactory.restoreFleetVehicleContract(
                    ContractNumber.generate(),
                    Customer.of("John Buyer", "ACME AG", "john@example.com"),
                    Vehicle.of("WBA00000000000101"),
                    Amount.ofCents(300000L, Currency.CHF),
                    Amount.ofCents(60000L, Currency.CHF),
                    Optional.empty()
                )
            ),
            Optional.empty()
        );
    }

    @Test
    void returnsFleetContractFromPort() {
        var number = ContractNumber.generate();
        var expected = aFleetContract(number);
        given(persistencePort.with(number)).willReturn(expected);

        var result = service.with(number);

        assertThat(result).isEqualTo(expected);
        then(persistencePort).should().with(number);
    }

    @Test
    void propagatesContractNotFoundExceptionFromPort() {
        var number = ContractNumber.generate();
        given(persistencePort.with(number)).willThrow(new ContractNotFoundException(number.number()));

        assertThatThrownBy(() -> service.with(number))
            .isInstanceOf(ContractNotFoundException.class);
    }
}
