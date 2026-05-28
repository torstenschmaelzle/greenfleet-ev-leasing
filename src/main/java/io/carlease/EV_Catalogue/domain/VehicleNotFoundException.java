package io.carlease.EV_Catalogue.domain;

public class VehicleNotFoundException extends VehicleDomainException {

    public VehicleNotFoundException(VehicleId vehicleId) {
        super("Vehicle not found: " + vehicleId.getVin());
    }
}
