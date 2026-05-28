package io.carlease.ev_catalogue.domain;

public abstract class VehicleDomainException extends RuntimeException {

    protected VehicleDomainException(String message) {
        super(message);
    }

}
