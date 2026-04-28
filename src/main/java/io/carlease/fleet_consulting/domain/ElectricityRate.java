package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class ElectricityRate {

    private final Amount ratePerKwh;

    private ElectricityRate(Amount ratePerKwh) {
        this.ratePerKwh = ratePerKwh;
    }

    public static ElectricityRate of(Amount ratePerKwh) {
        if (ratePerKwh == null) throw new InvalidFleetAttributeException("ratePerKwh", "must not be null");
        if (ratePerKwh.getAmountInCents() <= 0)
            throw new InvalidFleetAttributeException("ratePerKwh", "must be greater than 0");
        return new ElectricityRate(ratePerKwh);
    }

    public Amount getRatePerKwh() {
        return ratePerKwh;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ElectricityRate other = (ElectricityRate) obj;
        return ratePerKwh.equals(other.ratePerKwh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratePerKwh);
    }
}
