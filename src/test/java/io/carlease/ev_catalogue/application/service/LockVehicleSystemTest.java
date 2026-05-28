package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.LockVehicleUseCase;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.LockDuration;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LockVehicleSystemTest {

    @Autowired
    private LockVehicleUseCase lockVehicleUseCase;

    @Autowired
    private VehiclePersistencePort vehiclePersistancePort;

    @Test
    @Sql("LockVehicleSystemTest.sql")
    void lockVehicle_persistsReservedStatusAndLock() {
        VehicleId vehicleId = VehicleId.of("WBA00000000000003");

        lockVehicleUseCase.lock(vehicleId, LockDuration.ofMinutes(120));

        Vehicle locked = vehiclePersistancePort.findById(vehicleId).orElseThrow();
        assertThat(locked.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        assertThat(locked.getLock()).isPresent();
        assertThat(locked.getLock().get().isExpired()).isFalse();
    }
}
