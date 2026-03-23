package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;

@ValueObject
public class Percentage {

    private final BigDecimal value;

    private Percentage(BigDecimal value) {
        this.value = value;
    }

    public static Percentage of(BigDecimal value) {
        if (value == null) {
            throw new InvalidVehicleAttributeException("value", "must not be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidVehicleAttributeException("value", "must not be negative");
        }
        if (value.compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidVehicleAttributeException("value", "must not exceed 100");
        }
        return new Percentage(value);
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Percentage other = (Percentage) obj;
        return value.equals(other.value);
    }
}
