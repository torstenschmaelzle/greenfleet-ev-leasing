package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.assignVehicleToContract;
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
class AssignVehicleToContractSystemTest {

    @Autowired
    private assignVehicleToContract assignVehicleToContractUseCase;

    @Autowired
    private VehiclePersistancePort vehiclePersistancePort;

    @Test
    @Sql("AssignVehicleToContractSystemTest.sql")
    void assignVehicleToContract_persistsUnderContractStatusAndRemovesLock() {
        VehicleId vehicleId = VehicleId.of("WBA00000000000005");

        assignVehicleToContractUseCase.assign(vehicleId);

        Vehicle assigned = vehiclePersistancePort.findById(vehicleId).orElseThrow();
        assertThat(assigned.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        assertThat(assigned.getLock()).isEmpty();
    }
}
