package io.carlease.ev_catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public class EnergyConsumption {

    private final BigDecimal kwhPer100km;

    private EnergyConsumption(BigDecimal kwhPer100km) {
        this.kwhPer100km = kwhPer100km;
    }

    public static EnergyConsumption of(BigDecimal kwhPer100km) {
        if (kwhPer100km == null) {
            throw new InvalidVehicleAttributeException("kwhPer100km", "must not be null");
        }
        if (kwhPer100km.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidVehicleAttributeException("kwhPer100km", "must be greater than 0");
        }
        return new EnergyConsumption(kwhPer100km);
    }

    public BigDecimal getValue() {
        return kwhPer100km;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EnergyConsumption other = (EnergyConsumption) obj;
        return kwhPer100km.compareTo(other.kwhPer100km) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(kwhPer100km.stripTrailingZeros());
    }

}
