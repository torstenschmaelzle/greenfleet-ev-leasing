package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class NumberOfVehicles {

    private final int value;

    private NumberOfVehicles(int value) {
        this.value = value;
    }

    public static NumberOfVehicles of(int value) {
        if (value <= 0) throw new InvalidFleetAttributeException("value", "must be greater than 0");
        return new NumberOfVehicles(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NumberOfVehicles other = (NumberOfVehicles) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
