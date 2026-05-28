package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class ReleaseExpiredLocksServiceTest {

    private final VehiclePersistancePort persistancePort = mock(VehiclePersistancePort.class);
    private final VehicleBroadcastPort broadcaster = mock(VehicleBroadcastPort.class);
    private final ReleaseExpiredLocksService service = new ReleaseExpiredLocksService(persistancePort, broadcaster);

    private Vehicle reservedVehicle() {
        Vehicle vehicle = VehicleFactory.createVehicle(
                VehicleId.of("WBA12345678654234"),
                VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,
                        EnergyConsumption.of(new BigDecimal("17"))),
                BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11")),
                Performance.of(286, new BigDecimal("6.8"), 180),
                Amount.ofCents(75000L, Currency.CHF),
                Percentage.of(new BigDecimal("95")),
                Range.ofKm(440)
        );
        vehicle.lockVehicle(LockDuration.ofMinutes(120));
        return vehicle;
    }

    private void expireLock(Vehicle vehicle) throws Exception {
        ReservationLock lock = vehicle.getLock().orElseThrow();
        Field lockedAt = ReservationLock.class.getDeclaredField("lockedAt");
        lockedAt.setAccessible(true);
        lockedAt.set(lock, Instant.now().minus(3, ChronoUnit.HOURS));
    }

    @Nested
    class whenReservedVehiclesHaveExpiredLocks {

        @Test
        void releasesLockAndSavesVehicle() throws Exception {
            Vehicle vehicle = reservedVehicle();
            expireLock(vehicle);
            given(persistancePort.findAllReserved()).willReturn(List.of(vehicle));

            service.releaseExpiredLocks();

            then(persistancePort).should().save(vehicle);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void broadcastsAvailableStatus() throws Exception {
            Vehicle vehicle = reservedVehicle();
            expireLock(vehicle);
            given(persistancePort.findAllReserved()).willReturn(List.of(vehicle));

            service.releaseExpiredLocks();

            then(broadcaster).should().broadcast(vehicle.getVehicleId().getVin(), VehicleStatus.AVAILABLE);
        }
    }

    @Nested
    class whenReservedVehiclesHaveActiveLocks {

        @Test
        void doesNotSaveVehicleWithActiveLock() {
            Vehicle vehicle = reservedVehicle();
            given(persistancePort.findAllReserved()).willReturn(List.of(vehicle));

            service.releaseExpiredLocks();

            then(persistancePort).should(never()).save(any());
        }

        @Test
        void doesNotBroadcastForVehicleWithActiveLock() {
            Vehicle vehicle = reservedVehicle();
            given(persistancePort.findAllReserved()).willReturn(List.of(vehicle));

            service.releaseExpiredLocks();

            then(broadcaster).should(never()).broadcast(any(), any());
        }
    }

    @Nested
    class whenNoReservedVehiclesExist {

        @Test
        void doesNotSaveOrBroadcast() {
            given(persistancePort.findAllReserved()).willReturn(List.of());

            service.releaseExpiredLocks();

            then(persistancePort).should(never()).save(any());
            then(broadcaster).should(never()).broadcast(any(), any());
        }
    }
}
