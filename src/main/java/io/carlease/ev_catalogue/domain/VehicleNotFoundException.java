package io.carlease.ev_catalogue.domain;

public class VehicleNotFoundException extends VehicleDomainException {

    public VehicleNotFoundException(VehicleId vehicleId) {
        super("Vehicle not found: " + vehicleId.getVin());
    }
}
