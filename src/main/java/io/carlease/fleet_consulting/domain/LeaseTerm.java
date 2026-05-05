package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.Set;

@ValueObject
public class LeaseTerm {

    private static final Set<Integer> ALLOWED_MONTHS = Set.of(12, 24, 36, 48, 60);

    private final int months;

    private LeaseTerm(int months) {
        this.months = months;
    }

    public static LeaseTerm of(int months) {
        if (!ALLOWED_MONTHS.contains(months))
            throw new InvalidFleetAttributeException("months", "must be 24, 36, 48, or 60");
        return new LeaseTerm(months);
    }

    public int getMonths() {
        return months;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LeaseTerm other = (LeaseTerm) obj;
        return months == other.months;
    }

    @Override
    public int hashCode() {
        return Objects.hash(months);
    }
}
