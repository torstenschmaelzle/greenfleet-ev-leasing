package io.carlease.ev_catalogue.domain;

public class VehicleRemovalException extends VehicleDomainException {

    public VehicleRemovalException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Vehicle " + vehicleId.getVin() + " cannot be removed: current status is " + currentStatus);
    }
}
