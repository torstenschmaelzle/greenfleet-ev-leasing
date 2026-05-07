package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContractTest {

    private static final Customer CUSTOMER = Customer.of("John Buyer", "ACME AG", "john@example.com");

    private VehicleContract contract(String number) {
        return ContractFactory.createFleetVehicleContract(
                ContractNumber.of(number), CUSTOMER, Vehicle.of("Volkswagen ID.3"),
                Amount.ofCents(89780, Currency.EUR));
    }

    @Test
    void givenTwoContractsWithSameIdButDifferentFields_whenEquals_thenShouldReturnTrue() {
        var contract1 = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("4711"), CUSTOMER, Vehicle.of("Mercedes Benz C-Class"),
                Amount.ofCents(89780, Currency.EUR));
        var contract2 = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("4711"), Customer.of("Bob Myers", "Other Corp", "bob@example.com"),
                Vehicle.of("Volkswagen ID.3"), Amount.ofCents(50000, Currency.EUR));

        assertThat(contract1.equals(contract2)).isTrue();
    }

    @Test
    void givenTwoContractsWithDifferentIdButSameFields_whenEquals_thenShouldReturnFalse() {
        var contract1 = contract("4711");
        var contract2 = contract("4712");

        assertThat(contract1.equals(contract2)).isFalse();
    }

    @Test
    void givenTwoContractsWithSameId_whenHashcode_thenShouldBeEqual() {
        var contract1 = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("4711"), CUSTOMER, Vehicle.of("Mercedes Benz C-Class"),
                Amount.ofCents(89780, Currency.EUR));
        var contract2 = ContractFactory.createFleetVehicleContract(
                ContractNumber.of("4711"), Customer.of("Bob Myers", "Other Corp", "bob@example.com"),
                Vehicle.of("Volkswagen ID.3"), Amount.ofCents(50000, Currency.EUR));

        assertThat(contract1.hashCode()).isEqualTo(contract2.hashCode());
    }

    @Test
    void givenAContract_whenSign_thenContractIsSigned() {
        var c = contract("4711");

        c.sign(SignDate.of(2018, 12, 25));

        assertThat(c.isSigned()).isTrue();
        assertThat(c.signatureDate()).isEqualTo(SignDate.of(2018, 12, 25));
    }

    @Test
    void givenASignedContract_whenSignAgain_thenThrowsContractAlreadySignedException() {
        var c = contract("4711");
        c.sign(SignDate.of(2018, 12, 25));

        assertThatThrownBy(() -> c.sign(SignDate.of(2018, 12, 26)))
                .isInstanceOf(ContractAlreadySignedException.class);
    }

    @Test
    void givenAnUnsignedContract_whenSignatureDate_thenThrowsIllegalStateException() {
        var c = contract("4711");

        assertThatThrownBy(c::signatureDate)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void givenASignedContractData_whenRestoreFleetVehicleContract_thenContractIsSigned() {
        var contract = ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of("4711"),
                CUSTOMER,
                Vehicle.of("Mercedes Benz C-Class"),
                Amount.ofCents(89780, Currency.EUR),
                Amount.ofCents(89780, Currency.EUR),
                java.util.Optional.of(SignDate.of(2018, 12, 25))
        );

        assertThat(contract.isSigned()).isTrue();
        assertThat(contract.signatureDate()).isEqualTo(SignDate.of(2018, 12, 25));
    }
}
