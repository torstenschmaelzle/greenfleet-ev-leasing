package io.carlease.ev_catalogue.domain;

public class VehicleUnderContractViolationException extends VehicleDomainException {

    public VehicleUnderContractViolationException(VehicleId vehicleId, VehicleStatus currentStatus) {
        super("Vehicle " + vehicleId.getVin() + " cannot be placed under contract: current status is " + currentStatus);
    }
}
