package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class LockDuration {

    private final Integer hours;

    private LockDuration(Integer hours) {
        this.hours = hours;
    }

    public static LockDuration ofHours(Integer hours) {
        if (hours == null) {
            throw new InvalidVehicleAttributeException("hours", "must not be null");
        }
        if (hours <= 0) {
            throw new InvalidVehicleAttributeException("hours", "must be greater than 0");
        }
        return new LockDuration(hours);
    }

    public static LockDuration defaultDuration() {
        return ofHours(2);
    }

    public Integer getHours() {
        return hours;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LockDuration other = (LockDuration) obj;
        return hours.equals(other.hours);
    }
    @Override
    public int hashCode() {
        return Objects.hash(hours);
    }

}
