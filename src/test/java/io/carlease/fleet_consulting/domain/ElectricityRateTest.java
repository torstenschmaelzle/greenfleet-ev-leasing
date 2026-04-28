package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElectricityRateTest {

    @Nested
    class Validation {

        @Test
        void ratePerKwhMustNotBeNull() {
            assertThatThrownBy(() -> ElectricityRate.of(null))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void ratePerKwhMustBeGreaterThanZero() {
            Amount zeroCents = Amount.ofCents(0, Currency.CHF);

            assertThatThrownBy(() -> ElectricityRate.of(zeroCents))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }

    @Test
    void storesRateCorrectly() {
        Amount rate = Amount.ofCents(30, Currency.CHF);

        ElectricityRate electricityRate = ElectricityRate.of(rate);

        assertThat(electricityRate.getRatePerKwh()).isEqualTo(rate);
    }
}
