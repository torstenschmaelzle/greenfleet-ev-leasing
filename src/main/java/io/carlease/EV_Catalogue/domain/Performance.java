package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;

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
            throw new IllegalArgumentException("horsePowerHp must not be null");
        }
        if (accelerationSec == null) {
            throw new IllegalArgumentException("accelerationSec must not be null");
        }
        if (topSpeedKmh == null) {
            throw new IllegalArgumentException("topSpeedKmh must not be null");
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
        return horsePowerHp.equals(other.horsePowerHp)
                && accelerationSec.equals(other.accelerationSec)
                && topSpeedKmh.equals(other.topSpeedKmh);
    }
}
