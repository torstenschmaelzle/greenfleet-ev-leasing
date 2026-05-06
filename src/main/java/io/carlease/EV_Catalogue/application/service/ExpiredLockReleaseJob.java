package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredLockReleaseJob {

    private final VehiclePersistancePort vehiclePersistancePort;
    private final VehicleStatusBroadcaster broadcaster;

    public ExpiredLockReleaseJob(VehiclePersistancePort vehiclePersistancePort, VehicleStatusBroadcaster broadcaster) {
        this.vehiclePersistancePort = vehiclePersistancePort;
        this.broadcaster = broadcaster;
    }

    @Scheduled(fixedRate = 60_000) // runs every 60 seconds
    public void releaseExpiredLocks() {
        vehiclePersistancePort.findAllReserved().stream()
                .filter(v -> v.getLock().map(lock -> lock.isExpired()).orElse(false))
                .forEach(v -> {
                    v.releaseLock();
                    vehiclePersistancePort.save(v);
                    broadcaster.broadcast(v.getVehicleId().getVin(), "AVAILABLE");
                });
    }
}
