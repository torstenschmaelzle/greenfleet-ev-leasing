package io.carlease.EV_Catalogue.domain;

public abstract class VehicleDomainException extends RuntimeException {

    protected VehicleDomainException(String message) {
        super(message);
    }

}
