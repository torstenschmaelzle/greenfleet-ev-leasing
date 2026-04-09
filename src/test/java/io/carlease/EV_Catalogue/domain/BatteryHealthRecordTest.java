package io.carlease.EV_Catalogue.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BatteryHealthRecordTest {

    private Percentage validSoh() {
        return Percentage.of(new BigDecimal("95"));
    }

    private Range validRange() {
        return Range.ofKm(440);
    }

    @Test
    void stateOfHealthMustNotBeNull() {
        assertThatThrownBy(() -> new BatteryHealthMeasurement(null, validRange())).isInstanceOf(InvalidVehicleAttributeException.class);
    }

    @Test
    void measuredRangeKmMustNotBeNull() {
        assertThatThrownBy(() -> new BatteryHealthMeasurement(validSoh(), null)).isInstanceOf(InvalidVehicleAttributeException.class);
    }
}
