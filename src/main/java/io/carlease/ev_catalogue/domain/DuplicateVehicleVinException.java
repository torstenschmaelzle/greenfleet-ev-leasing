package io.carlease.ev_catalogue.domain;

public class DuplicateVehicleVinException extends VehicleDomainException {

    public DuplicateVehicleVinException(String vin) {
        super("A vehicle with VIN " + vin + " already exists in the catalogue.");
    }
}
