package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.util.UUID;

@Entity
public class BatteryHealthMeasurement {

    private final UUID measurementId;
    private final Instant measuredAt;
    private final Percentage stateOfHealth;
    private final Range measuredRangeKm;

    public BatteryHealthMeasurement(Percentage stateOfHealth, Range measuredRangeKm) {
        if (stateOfHealth == null) {
            throw new InvalidVehicleAttributeException("stateOfHealth", "must not be null");
        }
        if (measuredRangeKm == null) {
            throw new InvalidVehicleAttributeException("measuredRangeKm", "must not be null");
        }
        this.measurementId = UUID.randomUUID();
        this.measuredAt = Instant.now();
        this.stateOfHealth = stateOfHealth;
        this.measuredRangeKm = measuredRangeKm;
    }

    public UUID getMeasurementId() {
        return measurementId;
    }

    public Instant getMeasuredAt() {
        return measuredAt;
    }

    public Percentage getStateOfHealth() {
        return stateOfHealth;
    }

    public Range getMeasuredRangeKm() {
        return measuredRangeKm;
    }
}
