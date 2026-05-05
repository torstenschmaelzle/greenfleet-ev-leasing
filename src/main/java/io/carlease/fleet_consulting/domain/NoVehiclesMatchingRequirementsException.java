package io.carlease.fleet_consulting.domain;

public class NoVehiclesMatchingRequirementsException extends RuntimeException {

    public NoVehiclesMatchingRequirementsException(String reason) {
        super("No vehicles match your requirements: " + reason);
    }
}
