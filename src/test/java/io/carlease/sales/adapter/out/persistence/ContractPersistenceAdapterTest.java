package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ContractPersistenceAdapter.class)
public class ContractPersistenceAdapterTest {

    @Autowired
    private ContractPersistenceAdapter contractPersistenceAdapter;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @Sql("ContractPersistenceAdapterTest.sql")
    void loadsContract() {
        VehicleContract contract = contractPersistenceAdapter.with(new ContractNumber("1"));

        assertThat(contract.lessee()).isEqualTo(new Customer("John Buyer"));
        assertThat(contract.car()).isEqualTo(new Car("Volkswagen ID.3"));
        assertThat(contract.price()).isEqualTo(Amount.of(50000, Currency.EUR));
    }

    @Test
    void savesContract() {
        VehicleContract contract = new VehicleContract(ContractNumber.of("1"),
                Customer.of("John Buyer"),
                Car.of("Volkswagen ID.3"),
                Amount.of(50000, Currency.valueOf("EUR")));

        contractPersistenceAdapter.save(contract);

        assertThat(contractRepository.count()).isEqualTo(1);
    }

    @Test
    void savesAndLoadsSignedContract() {
        // given
        VehicleContract contract = new VehicleContract(
                ContractNumber.of("1"),
                Customer.of("John Buyer"),
                Car.of("Volkswagen ID.3"),
                Amount.of(50000, Currency.valueOf("EUR"))
        );

        contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));
        contract.approve(SignDate.of(2026, 3, 15));
        contract.sign(SignDate.of(2026, 3, 16));

        // when
        contractPersistenceAdapter.save(contract);
        VehicleContract loadedContract = contractPersistenceAdapter.with(ContractNumber.of("1"));

        // then
        assertThat(loadedContract.isCalculated()).isTrue();
        assertThat(loadedContract.isApproved()).isTrue();
        assertThat(loadedContract.isSigned()).isTrue();
        assertThat(loadedContract.signatureDate()).isEqualTo(SignDate.of(2026, 3, 16));
    }

}
