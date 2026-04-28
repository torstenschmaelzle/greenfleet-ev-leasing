package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Co2ImpactTest {

    private FleetRequirements validReq(int numberOfVehicles) {
        return FleetRequirements.of(
                Kilometre.of(100),
                NumberOfVehicles.of(numberOfVehicles),
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
        assertThatThrownBy(() -> Co2Impact.calculate(null, 20.0))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void calculatesEvGramsPerKmCorrectly() {
        // kwhPer100km=20, CO2 factor=12 g/kWh → evGrams = 20 * 12 / 100 = 2.4000
        Co2Impact co2 = Co2Impact.calculate(validReq(1), 20.0);

        assertThat(co2.getEvGramsPerKm()).isEqualByComparingTo(new BigDecimal("2.4000"));
    }

    @Test
    void calculatesAnnualSavingsKgCorrectly() {
        // evGrams=2.4, ICE baseline=150 g/km, dailyKm=100, vehicles=1
        // savings = (150 - 2.4) * 100 * 365 * 1 / 1000 = 5387.4000 kg
        Co2Impact co2 = Co2Impact.calculate(validReq(1), 20.0);

        assertThat(co2.getAnnualSavingsKg()).isEqualByComparingTo(new BigDecimal("5387.4000"));
    }

    @Test
    void annualSavingsScaleWithNumberOfVehicles() {
        // Same as above but vehicles=2 → savings doubles to 10774.8000 kg
        Co2Impact co2 = Co2Impact.calculate(validReq(2), 20.0);

        assertThat(co2.getAnnualSavingsKg()).isEqualByComparingTo(new BigDecimal("10774.8000"));
    }
}
