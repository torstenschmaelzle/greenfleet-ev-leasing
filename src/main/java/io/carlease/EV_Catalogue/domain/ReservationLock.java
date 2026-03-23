package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
class ReservationLock {

    private final UUID lockId;
    private final Instant lockedAt;
    private final Instant expiresAt;
    private final LockDuration duration;

    ReservationLock(LockDuration duration) {
        if (duration == null) {
            throw new IllegalArgumentException("duration must not be null");
        }
        this.lockId = UUID.randomUUID();
        this.lockedAt = Instant.now();
        this.expiresAt = lockedAt.plus(duration.getHours(), ChronoUnit.HOURS);
        this.duration = duration;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public UUID getLockId() {
        return lockId;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public LockDuration getDuration() {
        return duration;
    }
}
