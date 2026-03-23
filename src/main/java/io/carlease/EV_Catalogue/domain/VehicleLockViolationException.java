package io.carlease.EV_Catalogue.domain;

public class VehicleLockViolationException extends VehicleDomainException {

    public VehicleLockViolationException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Lock cannot be released for vehicle " + vehicleId.getId() + ": current status is " + currentStatus);
    }
}
