package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.LeaseTerm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FleetContractTest {

    private static final Customer CUSTOMER = Customer.of("John Buyer", "ACME AG", "john@example.com");
    private static final LeaseTerm LEASE_TERM = LeaseTerm.of(36);

    private VehicleContract vehicle(String contractNumber, long installmentCents) {
        return ContractFactory.createFleetVehicleContract(
                ContractNumber.of(contractNumber),
                CUSTOMER,
                Vehicle.of(contractNumber + "-VIN"),
                Amount.ofCents(installmentCents, Currency.CHF)
        );
    }

    // --- Construction ---

    @Test
    void givenEmptyVehicleList_whenConstructed_thenThrowsEmptyFleetException() {
        assertThatThrownBy(() -> ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED", List.of()))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    // --- totalMonthlyInstallment ---

    @Test
    void givenOneVehicle_whenTotalMonthlyInstallment_thenReturnsItsInstallment() {
        FleetContract fleet = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-001-V1", 60000)));

        assertThat(fleet.totalMonthlyInstallment()).isEqualTo(Amount.ofCents(60000, Currency.CHF));
    }

    @Test
    void givenTwoVehicles_whenTotalMonthlyInstallment_thenReturnsSumOfInstallments() {
        FleetContract fleet = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(
                        vehicle("FC-001-V1", 60000),
                        vehicle("FC-001-V2", 45000)
                ));

        assertThat(fleet.totalMonthlyInstallment()).isEqualTo(Amount.ofCents(105000, Currency.CHF));
    }

    // --- sign ---

    @Test
    void givenFleet_whenSign_thenFleetIsSigned() {
        FleetContract fleet = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-001-V1", 60000)));

        fleet.sign(SignDate.of(2025, 6, 1));

        assertThat(fleet.isSigned()).isTrue();
        assertThat(fleet.signatureDate()).isEqualTo(SignDate.of(2025, 6, 1));
    }

    @Test
    void givenFleet_whenSign_thenAllVehicleContractsAreSigned() {
        FleetContract fleet = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(
                        vehicle("FC-001-V1", 60000),
                        vehicle("FC-001-V2", 45000)
                ));

        fleet.sign(SignDate.of(2025, 6, 1));

        assertThat(fleet.vehicleContracts()).allMatch(VehicleContract::isSigned);
    }

    @Test
    void givenSignedFleet_whenSignAgain_thenThrowsContractAlreadySignedException() {
        FleetContract fleet = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-001-V1", 60000)));
        fleet.sign(SignDate.of(2025, 6, 1));

        assertThatThrownBy(() -> fleet.sign(SignDate.of(2025, 6, 2)))
                .isInstanceOf(ContractAlreadySignedException.class);
    }

    // --- Equality ---

    @Test
    void givenTwoFleetsWithSameNumber_whenEquals_thenTrue() {
        FleetContract fleet1 = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-001-V1", 60000)));
        FleetContract fleet2 = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"),
                Customer.of("Jane Smith", "Other Corp", "jane@other.com"),
                LeaseTerm.of(24), "COST_OPTIMIZED",
                List.of(vehicle("FC-001-V1", 99999)));

        assertThat(fleet1).isEqualTo(fleet2);
    }

    @Test
    void givenTwoFleetsWithDifferentNumber_whenEquals_thenFalse() {
        FleetContract fleet1 = ContractFactory.createFleetContract(
                ContractNumber.of("FC-001"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-001-V1", 60000)));
        FleetContract fleet2 = ContractFactory.createFleetContract(
                ContractNumber.of("FC-002"), CUSTOMER, LEASE_TERM, "BALANCED",
                List.of(vehicle("FC-002-V1", 60000)));

        assertThat(fleet1).isNotEqualTo(fleet2);
    }
}
