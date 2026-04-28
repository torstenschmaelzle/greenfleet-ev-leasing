package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TotalCostOfOwnershipTest {

    private FleetRequirements validReq() {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(1),
                ChargingSetup.of(
                        ChargingType.COMPANY_CHARGER,
                        ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
                ),
                Optional.of(Budget.of(Amount.ofCents(80000, Currency.CHF))),
                LeaseTerm.of(36),
                Optional.empty(),
                Optional.empty()
        );
    }

    @Test
    void reqMustNotBeNull() {
        assertThatThrownBy(() -> TotalCostOfOwnership.calculate(
                null, Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void installmentMustNotBeNull() {
        assertThatThrownBy(() -> TotalCostOfOwnership.calculate(validReq(), null, 20.0))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void calculatesMonthlyChargingCostCorrectly() {
        // dailyKm=100, vehicles=1, kwhPer100km=20, rate=30 cents/kWh
        // kWh/month = 100 * 30 * 1 * (20/100) = 600
        // chargingCents = 600 * 30 = 18000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getMonthlyChargingCost().getAmountInCents()).isEqualTo(18000);
    }

    @Test
    void monthlyLeaseCostIsInstallmentTimesNumberOfVehicles() {
        // installment=50000, vehicles=1 → monthlyLeaseCost=50000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getMonthlyLeaseCost().getAmountInCents()).isEqualTo(50000);
    }

    @Test
    void calculatesMonthlyTotalAsLeasePlusChargingCost() {
        // lease=50000, charging=18000 → monthly=68000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getMonthlyTotal().getAmountInCents()).isEqualTo(68000);
    }

    @Test
    void calculatesAnnualTotalAsTwelveTimesMonthlyTotal() {
        // monthly=68000 → annual=816000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getAnnualTotal().getAmountInCents()).isEqualTo(816000);
    }

    @Test
    void monthlyInstallmentPerVehicleMatchesInput() {
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getMonthlyInstallmentPerVehicle().getAmountInCents()).isEqualTo(50000);
    }

    @Test
    void monthlyLeaseCostScalesWithNumberOfVehicles() {
        // 3 vehicles, installment=50000 each → monthlyLeaseCost=150000
        FleetRequirements req = FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(3),
                ChargingSetup.of(
                        ChargingType.COMPANY_CHARGER,
                        ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
                ),
                Optional.empty(),
                LeaseTerm.of(36),
                Optional.empty(),
                Optional.empty()
        );

        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                req, Installment.of(Amount.ofCents(50000, Currency.CHF)), 20.0);

        assertThat(tco.getMonthlyLeaseCost().getAmountInCents()).isEqualTo(150000);
    }
}
