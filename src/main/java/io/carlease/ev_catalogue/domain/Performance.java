package io.carlease.ev_catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public class Performance {

    private final Integer horsePowerHp;
    private final BigDecimal accelerationSec;
    private final Integer topSpeedKmh;

    private Performance(Integer horsePowerHp, BigDecimal accelerationSec, Integer topSpeedKmh) {
        this.horsePowerHp = horsePowerHp;
        this.accelerationSec = accelerationSec;
        this.topSpeedKmh = topSpeedKmh;
    }

    public static Performance of(Integer horsePowerHp, BigDecimal accelerationSec, Integer topSpeedKmh) {
        if (horsePowerHp == null) {
            throw new InvalidVehicleAttributeException("horsePowerHp", "must not be null");
        }
        if (horsePowerHp <= 0) {
            throw new InvalidVehicleAttributeException("horsePowerHp", "must be greater than 0");
        }
        if (accelerationSec == null) {
            throw new InvalidVehicleAttributeException("accelerationSec", "must not be null");
        }
        if (accelerationSec.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidVehicleAttributeException("accelerationSec", "must be greater than 0");
        }
        if (topSpeedKmh == null) {
            throw new InvalidVehicleAttributeException("topSpeedKmh", "must not be null");
        }
        if (topSpeedKmh <= 0) {
            throw new InvalidVehicleAttributeException("topSpeedKmh", "must be greater than 0");
        }
        return new Performance(horsePowerHp, accelerationSec, topSpeedKmh);
    }

    public Integer getHorsePowerHp() {
        return horsePowerHp;
    }

    public BigDecimal getAccelerationSec() {
        return accelerationSec;
    }

    public Integer getTopSpeedKmh() {
        return topSpeedKmh;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Performance other = (Performance) obj;
        return horsePowerHp.compareTo(other.horsePowerHp) == 0
                && accelerationSec.compareTo(other.accelerationSec) == 0
                && topSpeedKmh.equals(other.topSpeedKmh);
    }
    @Override
    public int hashCode() {
        return Objects.hash(horsePowerHp, accelerationSec.stripTrailingZeros(), topSpeedKmh);
    }

}
