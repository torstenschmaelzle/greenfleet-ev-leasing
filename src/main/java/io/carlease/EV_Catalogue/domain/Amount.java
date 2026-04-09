package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class Amount {

    private final long amountInCents;
    private final Currency currency;

    private Amount(long amountInCents, Currency currency) {
        this.amountInCents = amountInCents;
        this.currency = currency;
    }


    public static Amount ofCents(long amountInCents, Currency currency) {
        if (currency == null) throw new InvalidVehicleAttributeException("currency", "must not be null");
        if (amountInCents < 0) throw new InvalidVehicleAttributeException("amountInCents", "must not be negative");
        return new Amount(amountInCents, currency);
    }


    public long getAmountInCents() {
        return amountInCents;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Amount add(Amount other) {
        if (other.currency != this.currency) {
            throw new InvalidVehicleAttributeException("currency", "amounts must have the same currency");
        }
        return Amount.ofCents(this.amountInCents + other.amountInCents, this.currency);
    }

    public Amount subtract(Amount other) {
        if (other.currency != this.currency) {
            throw new InvalidVehicleAttributeException("currency", "amounts must have the same currency");
        }
        return Amount.ofCents(this.amountInCents - other.amountInCents, this.currency);
    }

    public Amount convertTo(Currency target, double rate) {
        if (target == null) throw new InvalidVehicleAttributeException("currency", "must not be null");
        return Amount.ofCents(Math.round(this.amountInCents * rate), target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Amount other = (Amount) obj;
        return amountInCents == other.amountInCents && currency == other.currency;
    }
    @Override
    public int hashCode() {
        return Objects.hash(amountInCents, currency);
    }

}
