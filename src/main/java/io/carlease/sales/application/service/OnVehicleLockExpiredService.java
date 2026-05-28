package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.OnVehicleLockExpiredUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OnVehicleLockExpiredService implements OnVehicleLockExpiredUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public OnVehicleLockExpiredService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public void handle(List<String> vins) {
        Set<String> expiredVins = new HashSet<>(vins);
        fleetContractPersistencePort.listAll().stream()
                .filter(c -> !c.isSigned())
                .filter(c -> c.vehicleContracts().stream()
                        .anyMatch(vc -> expiredVins.contains(vc.vehicle().vin())))
                .forEach(c -> fleetContractPersistencePort.delete(c.number()));
    }
}
