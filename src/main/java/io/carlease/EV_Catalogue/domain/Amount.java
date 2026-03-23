package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Amount {

    private final long amountInCents;
    private final Currency currency;

    private Amount(long amountInCents, Currency currency) {
        this.amountInCents = amountInCents;
        this.currency = currency;
    }

    public static Amount of(long amountInCents, Currency currency) {
        if (currency == null) throw new InvalidVehicleAttributeException("currency", "must not be null");
        return new Amount(amountInCents, currency);
    }

    public static Amount ofCents(long amountInCents, Currency currency) {
        if (currency == null) throw new InvalidVehicleAttributeException("currency", "must not be null");
        return new Amount(amountInCents, currency);
    }

    public long getAmount() {
        return amountInCents;
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
        return Amount.of(this.amountInCents + other.amountInCents, this.currency);
    }

    public Amount subtract(Amount other) {
        if (other.currency != this.currency) {
            throw new InvalidVehicleAttributeException("currency", "amounts must have the same currency");
        }
        return Amount.of(this.amountInCents - other.amountInCents, this.currency);
    }

    public Amount convertTo(Currency target, double rate) {
        if (target == null) throw new InvalidVehicleAttributeException("currency", "must not be null");
        return Amount.of(Math.round(this.amountInCents * rate), target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Amount other = (Amount) obj;
        return amountInCents == other.amountInCents && currency == other.currency;
    }
}
