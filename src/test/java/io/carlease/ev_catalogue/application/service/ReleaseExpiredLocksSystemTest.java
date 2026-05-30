package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.ReleaseExpiredLocksUseCase;
import io.carlease.ev_catalogue.application.port.out.VehicleEventPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleId;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import io.carlease.sales.adapter.out.persistence.FleetContractRepository;
import io.carlease.sales.application.port.in.OnVehicleLockExpiredUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReleaseExpiredLocksSystemTest {

    @Autowired private ReleaseExpiredLocksUseCase releaseExpiredLocksUseCase;
    @Autowired private VehiclePersistencePort vehiclePersistencePort;
    @Autowired private FleetContractRepository fleetContractRepository;
    @Autowired private OnVehicleLockExpiredUseCase onVehicleLockExpiredUseCase;
    @Autowired private VehicleEventPort vehicleEventPort;

    @Test
    @Sql("ReleaseExpiredLocksDirectHandleTest.sql")
    void handle_deletesUnsignedFleetContractForExpiredVin() {
        onVehicleLockExpiredUseCase.handle(List.of("WBA00000000000997"));

        assertThat(fleetContractRepository.findById("fc-direct-001")).isEmpty();
    }

    @Test
    @Sql("ReleaseExpiredLocksDirectHandleTest.sql")
    void publishLocksExpired_eventChainDeletesUnsignedFleetContract() {
        vehicleEventPort.publishLocksExpired(List.of("WBA00000000000997"));

        assertThat(fleetContractRepository.findById("fc-direct-001")).isEmpty();
    }

    @Test
    @Sql("ReleaseExpiredLocksSystemTest.sql")
    void releaseExpiredLocks_releasesVehicle() {
        releaseExpiredLocksUseCase.releaseExpiredLocks();

        Vehicle vehicle = vehiclePersistencePort.findById(VehicleId.of("WBA00000000000996")).orElseThrow();
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(vehicle.getLock()).isEmpty();
    }
}
