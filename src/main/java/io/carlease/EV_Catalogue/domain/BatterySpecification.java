package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public class BatterySpecification {

    private final BigDecimal capacityKwh;
    private final BigDecimal voltageV;
    private final BigDecimal maxChargeSpeedKw;

    private BatterySpecification(BigDecimal capacityKwh, BigDecimal voltageV, BigDecimal maxChargeSpeedKw) {
        this.capacityKwh = capacityKwh;
        this.voltageV = voltageV;
        this.maxChargeSpeedKw = maxChargeSpeedKw;
    }

    public static BatterySpecification of(BigDecimal capacityKwh, BigDecimal voltageV, BigDecimal maxChargeSpeedKw) {
        if (capacityKwh == null) {
            throw new InvalidVehicleAttributeException("capacityKwh", "must not be null");
        }
        if (capacityKwh.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidVehicleAttributeException("capacityKwh", "must be greater than 0");
        }
        if (voltageV == null) {
            throw new InvalidVehicleAttributeException("voltageV", "must not be null");
        }
        if (voltageV.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidVehicleAttributeException("voltageV", "must be greater than 0");
        }
        if (maxChargeSpeedKw == null) {
            throw new InvalidVehicleAttributeException("maxChargeSpeedKw", "must not be null");
        }
        if (maxChargeSpeedKw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidVehicleAttributeException("maxChargeSpeedKw", "must be greater than 0");
        }
        return new BatterySpecification(capacityKwh, voltageV, maxChargeSpeedKw);
    }

    public BigDecimal getCapacityKwh() {
        return capacityKwh;
    }

    public BigDecimal getVoltageV() {
        return voltageV;
    }

    public BigDecimal getMaxChargeSpeedKw() {
        return maxChargeSpeedKw;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BatterySpecification other = (BatterySpecification) obj;
        return capacityKwh.compareTo(other.capacityKwh) == 0
                && voltageV.compareTo(other.voltageV) == 0
                && maxChargeSpeedKw.compareTo(other.maxChargeSpeedKw) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(
                capacityKwh.stripTrailingZeros(),
                voltageV.stripTrailingZeros(),
                maxChargeSpeedKw.stripTrailingZeros()
        );
    }

}
