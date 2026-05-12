package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.lockVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.LockDuration;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LockVehicleSystemTest {

    @Autowired
    private lockVehicle lockVehicleUseCase;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

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
