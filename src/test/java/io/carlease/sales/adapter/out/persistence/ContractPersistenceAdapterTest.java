package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(ContractPersistenceAdapter.class)
public class ContractPersistenceAdapterTest {

    private static final Customer CUSTOMER = Customer.of("John Buyer", "ACME AG", "john@example.com");

    @Autowired
    private ContractPersistenceAdapter contractPersistenceAdapter;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @Sql("ContractPersistenceAdapterTest.sql")
    void loadsContract() {
        VehicleContract contract = contractPersistenceAdapter.with(new ContractNumber("1"));

        assertThat(contract.lessee()).isEqualTo(CUSTOMER);
        assertThat(contract.vehicle()).isEqualTo(new Vehicle("Volkswagen ID.3"));
        assertThat(contract.installment()).isEqualTo(Amount.ofCents(89780, Currency.EUR));
    }

    @Test
    void savesContract() {
        VehicleContract contract = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("1"),
                CUSTOMER,
                Vehicle.of("Volkswagen ID.3"),
                Amount.ofCents(89780, Currency.EUR));

        contractPersistenceAdapter.save(contract);

        assertThat(contractRepository.count()).isEqualTo(1);
    }

    @Test
    void with_throwsContractNotFoundExceptionForUnknownNumber() {
        assertThatThrownBy(() -> contractPersistenceAdapter.with(ContractNumber.of("UNKNOWN")))
                .isInstanceOf(ContractNotFoundException.class);
    }

    @Test
    void savesAndLoadsSignedContract() {
        VehicleContract contract = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("1"),
                CUSTOMER,
                Vehicle.of("Volkswagen ID.3"),
                Amount.ofCents(89780, Currency.EUR));
        contract.sign(SignDate.of(2026, 3, 16));

        contractPersistenceAdapter.save(contract);
        VehicleContract loaded = contractPersistenceAdapter.with(ContractNumber.of("1"));

        assertThat(loaded.isSigned()).isTrue();
        assertThat(loaded.signatureDate()).isEqualTo(SignDate.of(2026, 3, 16));
        assertThat(loaded.lessee()).isEqualTo(CUSTOMER);
    }

}
