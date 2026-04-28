package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class ChargingSetup {

    private final ChargingType type;
    private final ElectricityRate electricityRate;

    private ChargingSetup(ChargingType type, ElectricityRate electricityRate) {
        this.type = type;
        this.electricityRate = electricityRate;
    }

    public static ChargingSetup of(ChargingType type, ElectricityRate electricityRate) {
        if (type == null) throw new InvalidFleetAttributeException("type", "must not be null");
        if (electricityRate == null) throw new InvalidFleetAttributeException("electricityRate", "must not be null");
        return new ChargingSetup(type, electricityRate);
    }

    public ChargingType getType() {
        return type;
    }

    public ElectricityRate getElectricityRate() {
        return electricityRate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChargingSetup other = (ChargingSetup) obj;
        return type == other.type && electricityRate.equals(other.electricityRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, electricityRate);
    }
}
