package io.carlease.ev_catalogue.domain;

public class VehicleNotAvailableException extends VehicleDomainException {

    public VehicleNotAvailableException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Vehicle " + vehicleId.getVin() + " cannot be locked: current status is " + currentStatus);
    }
}
