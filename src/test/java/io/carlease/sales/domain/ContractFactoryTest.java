package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ContractFactoryTest {

    private static final Customer CUSTOMER = Customer.of("John Buyer", "ACME AG", "john@example.com");

    // --- restoreFleetVehicleContract ---

    @Test
    void givenFleetVehicleData_whenRestoreFleetVehicleContract_thenContractContainsRestoredData() {
        VehicleContract contract = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of("4711"),
                CUSTOMER,
                Vehicle.of("Mercedes Benz C-Class"),
                Amount.ofCents(0, Currency.EUR),
                Amount.ofCents(89780, Currency.EUR),
                Optional.empty());

        assertThat(contract.number()).isEqualTo(ContractNumber.of("4711"));
        assertThat(contract.lessee()).isEqualTo(CUSTOMER);
        assertThat(contract.vehicle()).isEqualTo(Vehicle.of("Mercedes Benz C-Class"));
        assertThat(contract.installment()).isEqualTo(Amount.ofCents(89780, Currency.EUR));
        assertThat(contract.isSigned()).isFalse();
    }

    @Test
    void givenFleetVehicleDataWithSignDate_whenRestoreFleetVehicleContract_thenContractIsSigned() {
        VehicleContract contract = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of("4711"),
                CUSTOMER,
                Vehicle.of("Mercedes Benz C-Class"),
                Amount.ofCents(0, Currency.EUR),
                Amount.ofCents(89780, Currency.EUR),
                Optional.of(SignDate.of(2024, 6, 1)));

        assertThat(contract.isSigned()).isTrue();
        assertThat(contract.signatureDate()).isEqualTo(SignDate.of(2024, 6, 1));
    }

    // --- restoreFleetContract ---

    @Test
    void givenFleetData_whenRestoreFleetContract_thenFleetContainsRestoredData() {
        VehicleContract vc = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of("FC-001-V1"),
                CUSTOMER,
                Vehicle.of("FC-001-V1-VIN"),
                Amount.ofCents(60000, Currency.CHF),
                Amount.ofCents(60000, Currency.CHF),
                Optional.empty());

        FleetContract fleet = ContractFactory.restoreFleetContract(
                ContractNumber.of("FC-001"),
                CUSTOMER,
                LeaseTerm.of(36),
                "ECO_FOCUSED",
                List.of(vc),
                Optional.empty());

        assertThat(fleet.number()).isEqualTo(ContractNumber.of("FC-001"));
        assertThat(fleet.customer()).isEqualTo(CUSTOMER);
        assertThat(fleet.leaseTerm()).isEqualTo(LeaseTerm.of(36));
        assertThat(fleet.bundleType()).isEqualTo("ECO_FOCUSED");
        assertThat(fleet.vehicleContracts()).hasSize(1);
        assertThat(fleet.isSigned()).isFalse();
    }

    @Test
    void givenFleetDataWithSignDate_whenRestoreFleetContract_thenFleetIsSigned() {
        VehicleContract vc = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of("FC-001-V1"),
                CUSTOMER,
                Vehicle.of("FC-001-V1-VIN"),
                Amount.ofCents(60000, Currency.CHF),
                Amount.ofCents(60000, Currency.CHF),
                Optional.of(SignDate.of(2024, 6, 1)));

        FleetContract fleet = ContractFactory.restoreFleetContract(
                ContractNumber.of("FC-001"),
                CUSTOMER,
                LeaseTerm.of(36),
                "ECO_FOCUSED",
                List.of(vc),
                Optional.of(SignDate.of(2024, 6, 1)));

        assertThat(fleet.isSigned()).isTrue();
        assertThat(fleet.signatureDate()).isEqualTo(SignDate.of(2024, 6, 1));
    }
}
