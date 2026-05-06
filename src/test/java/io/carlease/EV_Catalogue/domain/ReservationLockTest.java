package io.carlease.EV_Catalogue.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationLockTest {

    //helper methods
    private ReservationLock lockWithMinutes(int minutes) {
        return new ReservationLock(LockDuration.ofMinutes(minutes));
    }

    //Sets the lockedAt field of a ReservationLock instance to a specific Instant using reflection
    private void setLockedAt(ReservationLock lock, Instant instant) throws Exception {
        Field field = ReservationLock.class.getDeclaredField("lockedAt");
        //bypass private access modifier
        field.setAccessible(true);
        field.set(lock, instant);
    }

    //tests
    @Nested
    class lockExpiry {

        @Test
        void expiredLockIsDetected() throws Exception {
            var lock = lockWithMinutes(120);
            // Set lockedAt to 3 hours ago so that lockedAt + 2h is 1 hour in the past
            setLockedAt(lock, Instant.now().minus(3, ChronoUnit.HOURS));
            assertThat(lock.isExpired()).isTrue();
        }

        @Test
        void activeLockIsNotExpired() {
            var lock = lockWithMinutes(120);
            assertThat(lock.isExpired()).isFalse();
        }
    }
}
