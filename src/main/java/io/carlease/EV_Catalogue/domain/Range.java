package io.carlease.ev_catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class Range {

    private final Integer valueKm;

    private Range(Integer valueKm) {
        this.valueKm = valueKm;
    }

    public static Range ofKm(Integer valueKm) {
        if (valueKm == null) {
            throw new InvalidVehicleAttributeException("valueKm", "must not be null");
        }
        if (valueKm <= 0) {
            throw new InvalidVehicleAttributeException("valueKm", "must be greater than 0");
        }
        return new Range(valueKm);
    }

    public Integer getValue() { return valueKm; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Range other = (Range) obj;
        return valueKm.equals(other.valueKm);
    }
    @Override
    public int hashCode() {
        return Objects.hash(valueKm);
    }

}
