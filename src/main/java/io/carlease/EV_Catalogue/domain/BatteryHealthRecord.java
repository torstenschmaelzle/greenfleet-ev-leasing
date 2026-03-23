package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.util.UUID;

@Entity
class BatteryHealthRecord {

    private final UUID recordId;
    private final Instant measuredAt;
    private final Percentage stateOfHealth;
    private final Range measuredRangeKm;

    BatteryHealthRecord(Percentage stateOfHealth, Range measuredRangeKm) {
        if (stateOfHealth == null) {
            throw new IllegalArgumentException("stateOfHealth must not be null");
        }
        if (measuredRangeKm == null) {
            throw new IllegalArgumentException("measuredRangeKm must not be null");
        }
        this.recordId = UUID.randomUUID();
        this.measuredAt = Instant.now();
        this.stateOfHealth = stateOfHealth;
        this.measuredRangeKm = measuredRangeKm;
    }

    public UUID getRecordId() {
        return recordId;
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
