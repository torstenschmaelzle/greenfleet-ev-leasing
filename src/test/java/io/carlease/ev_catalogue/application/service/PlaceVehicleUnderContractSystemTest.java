package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.PlaceVehicleUnderContractUseCase;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaceVehicleUnderContractSystemTest {

    @Autowired
    private PlaceVehicleUnderContractUseCase placeVehicleUnderContractUseCase;

    @Autowired
    private VehiclePersistencePort vehiclePersistancePort;

    @Test
    @Sql("PlaceVehicleUnderContractSystemTest.sql")
    void placeVehicleUnderContract_persistsUnderContractStatusAndRemovesLock() {
        VehicleId vehicleId = VehicleId.of("WBA00000000000005");

        placeVehicleUnderContractUseCase.assign(vehicleId);

        Vehicle assigned = vehiclePersistancePort.findById(vehicleId).orElseThrow();
        assertThat(assigned.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        assertThat(assigned.getLock()).isEmpty();
    }
}
