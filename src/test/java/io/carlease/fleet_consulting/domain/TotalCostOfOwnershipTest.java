package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TotalCostOfOwnershipTest {

    private FleetRequirements validReq() {
        ChargingSetup setup = ChargingSetup.of(
                ChargingType.COMPANY_CHARGER,
                ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
        );
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfDrivers.of(1),
                setup,
                Budget.of(Amount.ofCents(80000, Currency.CHF)),
                VehicleType.SUV,
                ScoringWeights.defaultWeights()
        );
    }

    @Test
    void reqMustNotBeNull() {
        assertThatThrownBy(() -> TotalCostOfOwnership.calculate(null, Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(300)))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void monthlyLeasePriceMustNotBeNull() {
        assertThatThrownBy(() -> TotalCostOfOwnership.calculate(validReq(), null, 20.0, Kilometre.of(300)))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void calculatesMonthlyChargingCostCorrectly() {
        // dailyKm=100, drivers=1, kwhPer100km=20, rate=30 cents/kWh
        // kWh/month = 100 * 30 * 1 * (20/100) = 600
        // chargingCents = 600 * 30 = 18000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(300));

        assertThat(tco.getMonthlyChargingCost().getAmountInCents()).isEqualTo(18000);
    }

    @Test
    void calculatesMonthlyTotalAsLeasePlusChargingCost() {
        // lease=50000, charging=18000 → monthly=68000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(300));

        assertThat(tco.getMonthlyTotal().getAmountInCents()).isEqualTo(68000);
    }

    @Test
    void calculatesAnnualTotalAsTwelveTimesMonthlyTotal() {
        // monthly=68000 → annual=816000
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(
                validReq(), Amount.ofCents(50000, Currency.CHF), 20.0, Kilometre.of(300));

        assertThat(tco.getAnnualTotal().getAmountInCents()).isEqualTo(816000);
    }
}
