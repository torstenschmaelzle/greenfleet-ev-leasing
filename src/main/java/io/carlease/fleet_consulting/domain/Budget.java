package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class Budget {

    private final Amount monthlyMax;

    private Budget(Amount monthlyMax) {
        this.monthlyMax = monthlyMax;
    }

    public static Budget of(Amount monthlyMax) {
        if (monthlyMax == null) throw new InvalidFleetAttributeException("monthlyMax", "must not be null");
        return new Budget(monthlyMax);
    }

    public Amount getMonthlyMax() {
        return monthlyMax;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Budget other = (Budget) obj;
        return monthlyMax.equals(other.monthlyMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyMax);
    }
}
