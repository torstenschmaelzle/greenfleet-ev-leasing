package io.carlease.fleet_consulting.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Co2ImpactTest {

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
        assertThatThrownBy(() -> Co2Impact.calculate(null, 20.0))
                .isInstanceOf(InvalidFleetAttributeException.class);
    }

    @Test
    void calculatesEvGramsPerKmCorrectly() {
        // kwhPer100km=20, CO2 factor=12 g/kWh → evGrams = 20 * 12 / 100 = 2.4000
        Co2Impact co2 = Co2Impact.calculate(validReq(), 20.0);

        assertThat(co2.getEvGramsPerKm()).isEqualByComparingTo(new BigDecimal("2.4000"));
    }

    @Test
    void calculatesAnnualSavingsKgCorrectly() {
        // evGrams=2.4, ICE baseline=150 g/km, dailyKm=100, drivers=1
        // savings = (150 - 2.4) * 100 * 365 * 1 / 1000 = 5387.4000 kg
        Co2Impact co2 = Co2Impact.calculate(validReq(), 20.0);

        assertThat(co2.getAnnualSavingsKg()).isEqualByComparingTo(new BigDecimal("5387.4000"));
    }

    @Test
    void annualSavingsScaleWithNumberOfDrivers() {
        // Same as above but drivers=2 → savings doubles to 10774.8000 kg
        ChargingSetup setup = ChargingSetup.of(
                ChargingType.COMPANY_CHARGER,
                ElectricityRate.of(Amount.ofCents(30, Currency.CHF))
        );
        FleetRequirements reqTwoDrivers = FleetRequirements.of(
                Kilometre.of(100),
                NumberOfDrivers.of(2),
                setup,
                Budget.of(Amount.ofCents(80000, Currency.CHF)),
                VehicleType.SUV,
                ScoringWeights.defaultWeights()
        );

        Co2Impact co2 = Co2Impact.calculate(reqTwoDrivers, 20.0);

        assertThat(co2.getAnnualSavingsKg()).isEqualByComparingTo(new BigDecimal("10774.8000"));
    }
}
