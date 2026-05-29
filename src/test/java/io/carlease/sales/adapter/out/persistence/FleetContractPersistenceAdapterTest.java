package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;
import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({FleetContractPersistenceAdapter.class, ContractPersistenceAdapter.class})
class FleetContractPersistenceAdapterTest {

    @Autowired private FleetContractPersistenceAdapter adapter;
    @Autowired private FleetContractRepository fleetContractRepository;
    @Autowired private ContractRepository contractRepository;

    private VehicleContract aVehicleContract(String vin) {
        return ContractFactory.restoreFleetVehicleContract(
            ContractNumber.generate(),
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            Vehicle.of(vin),
            Amount.ofCents(300000L, Currency.CHF),
            Amount.ofCents(60000L, Currency.CHF),
            Optional.empty()
        );
    }

    private FleetContract aFleetContractWith(String... vins) {
        var vehicleContracts = Arrays.stream(vins)
            .map(this::aVehicleContract)
            .toList();
        return ContractFactory.createFleetContract(
            ContractNumber.generate(),
            Customer.of("John Buyer", "ACME AG", "john@example.com"),
            LeaseTerm.of(36),
            BundleType.BALANCED,
            vehicleContracts
        );
    }

    @Test
    void save_persistsFleetContractRow() {
        var contract = aFleetContractWith("WBA00000000000101");

        adapter.save(contract);

        assertThat(fleetContractRepository.existsById(contract.number().number())).isTrue();
    }

    @Test
    void save_persistsAllVehicleContractRows() {
        var contract = aFleetContractWith("WBA00000000000101", "WBA00000000000102");

        adapter.save(contract);

        assertThat(contractRepository.count()).isEqualTo(2);
    }

    @Test
    void with_returnsFleetContractWithAllVehicleContracts() {
        var contract = aFleetContractWith("WBA00000000000101", "WBA00000000000102");
        adapter.save(contract);

        var loaded = adapter.with(contract.number());

        assertThat(loaded.number()).isEqualTo(contract.number());
        assertThat(loaded.vehicleContracts()).hasSize(2);
        assertThat(loaded.customer().email()).isEqualTo("john@example.com");
    }

    @Test
    void with_throwsContractNotFoundExceptionForUnknownNumber() {
        assertThatThrownBy(() -> adapter.with(ContractNumber.generate()))
            .isInstanceOf(ContractNotFoundException.class);
    }
}
