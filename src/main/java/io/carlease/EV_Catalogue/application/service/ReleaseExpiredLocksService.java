package io.carlease.ev_catalogue.application.service;

import io.carlease.ev_catalogue.application.port.in.ReleaseExpiredLocksUseCase;
import io.carlease.ev_catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.ev_catalogue.application.port.out.VehicleEventPort;
import io.carlease.ev_catalogue.application.port.out.VehiclePersistencePort;
import io.carlease.ev_catalogue.domain.ReservationLock;
import io.carlease.ev_catalogue.domain.Vehicle;
import io.carlease.ev_catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReleaseExpiredLocksService implements ReleaseExpiredLocksUseCase {

    private final VehiclePersistencePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;
    private final VehicleEventPort vehicleEventPort;

    public ReleaseExpiredLocksService(VehiclePersistencePort vehiclePersistancePort,
                                      VehicleBroadcastPort broadcaster,
                                      VehicleEventPort vehicleEventPort) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
        this.vehicleEventPort = vehicleEventPort;
    }

    @Override
    public void releaseExpiredLocks() {
        List<Vehicle> expired = vehiclePersistancePort.findAllReserved().stream()
                .filter(v -> v.getLock().map(ReservationLock::isExpired).orElse(false))
                .toList();

        List<String> expiredVins = new ArrayList<>();
        for (Vehicle v : expired) {
            v.releaseLock();
            vehiclePersistancePort.save(v);
            broadcaster.broadcast(v.getVehicleId().getVin(), VehicleStatus.AVAILABLE);
            expiredVins.add(v.getVehicleId().getVin());
        }

        if (!expiredVins.isEmpty()) {
            vehicleEventPort.publishLocksExpired(expiredVins);
        }
    }
}
