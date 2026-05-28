package io.carlease.EV_Catalogue.application.port.out;

import io.carlease.EV_Catalogue.domain.VehicleStatus;

public interface VehicleBroadcastPort {
    void broadcast(String vin, VehicleStatus status);
}
