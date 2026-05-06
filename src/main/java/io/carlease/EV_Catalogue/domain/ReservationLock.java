package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
public class ReservationLock {

    private final UUID lockId;
    private final Instant lockedAt;
    private final LockDuration duration;

    public ReservationLock(LockDuration duration) {
        if (duration == null) {
            throw new InvalidVehicleAttributeException("duration", "must not be null");
        }
        this.lockId = UUID.randomUUID();
        this.lockedAt = Instant.now();
        this.duration = duration;
    }


    public boolean isExpired() {
        return Instant.now().isAfter(getExpiresAt());
    }

    public UUID getLockId() {
        return lockId;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public Instant getExpiresAt() {
        return lockedAt.plus(duration.getMinutes(), ChronoUnit.MINUTES);
    }

    public LockDuration getDuration() {
        return duration;
    }
}
