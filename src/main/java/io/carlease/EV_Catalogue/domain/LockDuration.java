package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class LockDuration {

    private final Integer hours;

    private LockDuration(Integer hours) {
        this.hours = hours;
    }

    public static LockDuration ofHours(Integer hours) {
        if (hours == null) {
            throw new IllegalArgumentException("Lock duration must not be null");
        }
        if (hours <= 0) {
            throw new IllegalArgumentException("Lock duration must be greater than 0 hours");
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
}
