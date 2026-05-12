package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.releaseVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReleaseVehicleSystemTest {

    @Autowired
    private releaseVehicle releaseVehicleUseCase;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

    @Test
    @Sql("ReleaseVehicleSystemTest.sql")
    void releaseVehicle_persistsAvailableStatusAndRemovesLock() {
        VehicleId vehicleId = VehicleId.of("WBA00000000000004");

        releaseVehicleUseCase.release(vehicleId);

        Vehicle released = vehiclePersistancePort.findById(vehicleId).orElseThrow();
        assertThat(released.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(released.getLock()).isEmpty();
    }
}
