package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class RangeFit {

    private final Kilometre requiredKm;
    private final Kilometre actualKm;
    private final boolean sufficient;

    private RangeFit(Kilometre requiredKm, Kilometre actualKm, boolean sufficient) {
        this.requiredKm = requiredKm;
        this.actualKm = actualKm;
        this.sufficient = sufficient;
    }

    public static RangeFit evaluate(Kilometre dailyKm, Kilometre vehicleRange) {
        if (dailyKm == null) throw new InvalidFleetAttributeException("dailyKm", "must not be null");
        if (vehicleRange == null) throw new InvalidFleetAttributeException("vehicleRange", "must not be null");
        int requiredValue = (int) Math.ceil(dailyKm.getValue() * DomainConstants.RANGE_SAFETY_FACTOR);
        Kilometre requiredKm = Kilometre.of(requiredValue);
        boolean sufficient = vehicleRange.getValue() >= requiredValue;
        return new RangeFit(requiredKm, vehicleRange, sufficient);
    }

    public Kilometre getRequiredKm() {
        return requiredKm;
    }

    public Kilometre getActualKm() {
        return actualKm;
    }

    public boolean isSufficient() {
        return sufficient;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RangeFit other = (RangeFit) obj;
        return sufficient == other.sufficient
                && requiredKm.equals(other.requiredKm)
                && actualKm.equals(other.actualKm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredKm, actualKm, sufficient);
    }
}
