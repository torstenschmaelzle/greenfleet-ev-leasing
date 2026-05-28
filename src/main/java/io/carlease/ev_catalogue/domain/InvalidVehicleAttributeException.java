package io.carlease.ev_catalogue.domain;

public class InvalidVehicleAttributeException extends VehicleDomainException {

    public InvalidVehicleAttributeException(String attributeName, String reason) {
        super("Invalid value for " + attributeName + ": " + reason);
    }
}
