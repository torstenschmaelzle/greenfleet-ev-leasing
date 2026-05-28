package io.carlease.ev_catalogue.application.port.out;

import io.carlease.ev_catalogue.domain.VehicleStatus;

public interface VehicleBroadcastPort {
    void broadcast(String vin, VehicleStatus status);
}
