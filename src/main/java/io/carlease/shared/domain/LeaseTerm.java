package io.carlease.shared.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.Set;

/**
 * Lease duration in months. Only 12, 24, 36, 48, or 60 months are valid —
 * matching the options offered to customers in the frontend.
 */
@ValueObject
public class LeaseTerm {

    public static final Set<Integer> ALLOWED_MONTHS = Set.of(12, 24, 36, 48, 60);

    private final int months;

    private LeaseTerm(int months) {
        this.months = months;
    }

    public static LeaseTerm of(int months) {
        if (!ALLOWED_MONTHS.contains(months))
            throw new IllegalArgumentException(
                    "leaseTermMonths must be 12, 24, 36, 48, or 60, got: " + months);
        return new LeaseTerm(months);
    }

    public static LeaseTerm ofMonths(int months) {
        return of(months);
    }

    public static LeaseTerm ofYears(int years) {
        return of(years * 12);
    }

    public int getMonths() {
        return months;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return months == ((LeaseTerm) obj).months;
    }

    @Override
    public int hashCode() {
        return Objects.hash(months);
    }

    @Override
    public String toString() {
        return "LeaseTerm[" + months + " months]";
    }
}
