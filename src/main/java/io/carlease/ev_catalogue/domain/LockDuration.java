package io.carlease.ev_catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class LockDuration {

    private final Integer minutes;

    private LockDuration(Integer minutes) {
        this.minutes = minutes;
    }

    public static LockDuration ofMinutes(Integer minutes) {
        if (minutes == null) {
            throw new InvalidVehicleAttributeException("minutes", "must not be null");
        }
        if (minutes <= 0) {
            throw new InvalidVehicleAttributeException("minutes", "must be greater than 0");
        }
        return new LockDuration(minutes);
    }

    public static LockDuration defaultDuration() {
        return ofMinutes(15);
    }

    public Integer getMinutes() {
        return minutes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LockDuration other = (LockDuration) obj;
        return minutes.equals(other.minutes);
    }
    @Override
    public int hashCode() {
        return Objects.hash(minutes);
    }

}
