package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.Amount;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class Installment {

    private final Amount monthlyAmount;

    private Installment(Amount monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public static Installment of(Amount monthlyAmount) {
        if (monthlyAmount == null)
            throw new InvalidFleetAttributeException("monthlyAmount", "must not be null");
        return new Installment(monthlyAmount);
    }

    public Amount getMonthlyAmount() {
        return monthlyAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Installment other = (Installment) obj;
        return monthlyAmount.equals(other.monthlyAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyAmount);
    }
}
