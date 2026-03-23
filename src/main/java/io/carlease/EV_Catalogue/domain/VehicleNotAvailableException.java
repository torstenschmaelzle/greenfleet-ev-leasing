package io.carlease.EV_Catalogue.domain;

public class VehicleNotAvailableException extends VehicleDomainException {

    public VehicleNotAvailableException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Vehicle " + vehicleId.getId() + " cannot be locked: current status is " + currentStatus);
    }
}
