package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;

@ValueObject
public class EnergyConsumption {

    private final BigDecimal kwhPer100km;

    private EnergyConsumption(BigDecimal kwhPer100km) {
        this.kwhPer100km = kwhPer100km;
    }

    public static EnergyConsumption of(BigDecimal kwhPer100km) {
        if (kwhPer100km == null) {
            throw new IllegalArgumentException("Energy consumption must not be null");
        }
        if (kwhPer100km.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Energy consumption must be greater than 0");
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
        return kwhPer100km.equals(other.kwhPer100km);
    }
}
