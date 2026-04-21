package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class Kilometre {

    private final int value;

    private Kilometre(int value) {
        this.value = value;
    }

    public static Kilometre of(int value) {
        if (value <= 0) throw new InvalidFleetAttributeException("value", "must be greater than 0");
        return new Kilometre(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Kilometre other = (Kilometre) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
