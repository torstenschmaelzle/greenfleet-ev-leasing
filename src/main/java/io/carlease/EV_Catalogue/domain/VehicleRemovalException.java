package io.carlease.EV_Catalogue.domain;

public class VehicleRemovalException extends VehicleDomainException {

    public VehicleRemovalException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Vehicle " + vehicleId.getId() + " cannot be removed: current status is " + currentStatus);
    }
}
