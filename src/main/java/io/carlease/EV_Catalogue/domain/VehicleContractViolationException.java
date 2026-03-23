package io.carlease.EV_Catalogue.domain;

public class VehicleContractViolationException extends VehicleDomainException {

    public VehicleContractViolationException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Contract cannot be confirmed for vehicle " + vehicleId.getId() + ": current status is " + currentStatus);
    }
}
