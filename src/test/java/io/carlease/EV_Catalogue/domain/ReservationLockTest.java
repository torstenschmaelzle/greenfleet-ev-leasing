package io.carlease.EV_Catalogue.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationLockTest {

    //helper methods
    private ReservationLock lockWithHours(int hours) {
        return new ReservationLock(LockDuration.ofHours(hours));
    }

    //Sets the expiresAt field of a ReservationLock instance to a specific Instant using reflection
    private void setExpiresAt(ReservationLock lock, Instant instant) throws Exception {
        Field field = ReservationLock.class.getDeclaredField("expiresAt");
        //bypass private access modifier
        field.setAccessible(true);
        field.set(lock, instant);
    }

    //tests
    @Nested
    class lockExpiry {

        @Test
        void expiredLockIsDetected() throws Exception {
            var lock = lockWithHours(1);
            setExpiresAt(lock, Instant.now().minus(1, ChronoUnit.HOURS));
            assertThat(lock.isExpired()).isTrue();
        }

        @Test
        void activeLockIsNotExpired() {
            var lock = lockWithHours(1);
            assertThat(lock.isExpired()).isFalse();
        }
    }
}
