package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@ValueObject
public class Co2Impact {

    private final BigDecimal evGramsPerKm;
    private final BigDecimal annualSavingsKg;

    private Co2Impact(BigDecimal evGramsPerKm, BigDecimal annualSavingsKg) {
        this.evGramsPerKm = evGramsPerKm;
        this.annualSavingsKg = annualSavingsKg;
    }

    public static Co2Impact calculate(FleetRequirements req, double kwhPer100km) {
        if (req == null) throw new InvalidFleetAttributeException("req", "must not be null");

        BigDecimal evGramsPerKm = BigDecimal.valueOf(kwhPer100km)
                .multiply(BigDecimal.valueOf(DomainConstants.CO2_FACTOR_PER_KWH))
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        int dailyKm = req.getDailyRouteKm().getValue();
        int drivers = req.getNumberOfDrivers().getValue();

        BigDecimal annualSavingsKg = BigDecimal.valueOf(DomainConstants.ICE_GRAMS_PER_KM)
                .subtract(evGramsPerKm)
                .multiply(BigDecimal.valueOf(dailyKm))
                .multiply(BigDecimal.valueOf(365))
                .multiply(BigDecimal.valueOf(drivers))
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);

        return new Co2Impact(evGramsPerKm, annualSavingsKg);
    }

    public BigDecimal getEvGramsPerKm() {
        return evGramsPerKm;
    }

    public BigDecimal getAnnualSavingsKg() {
        return annualSavingsKg;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Co2Impact other = (Co2Impact) obj;
        return evGramsPerKm.compareTo(other.evGramsPerKm) == 0
                && annualSavingsKg.compareTo(other.annualSavingsKg) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(evGramsPerKm.stripTrailingZeros(), annualSavingsKg.stripTrailingZeros());
    }
}
