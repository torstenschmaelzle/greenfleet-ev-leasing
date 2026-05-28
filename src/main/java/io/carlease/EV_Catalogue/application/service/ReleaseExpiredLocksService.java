package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.ReleaseExpiredLocksUseCase;
import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.ReservationLock;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.springframework.stereotype.Service;

@Service
public class ReleaseExpiredLocksService implements ReleaseExpiredLocksUseCase {

    private final VehiclePersistancePort vehiclePersistancePort;
    private final VehicleBroadcastPort broadcaster;

    public ReleaseExpiredLocksService(VehiclePersistancePort vehiclePersistancePort,
                                      VehicleBroadcastPort broadcaster) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
    }

    @Override
    public void releaseExpiredLocks() {
        vehiclePersistancePort.findAllReserved().stream()
                .filter(v -> v.getLock().map(ReservationLock::isExpired).orElse(false))
                .forEach(v -> {
                    v.releaseLock();
                    vehiclePersistancePort.save(v);
                    broadcaster.broadcast(v.getVehicleId().getVin(), VehicleStatus.AVAILABLE);
                });
    }
}
