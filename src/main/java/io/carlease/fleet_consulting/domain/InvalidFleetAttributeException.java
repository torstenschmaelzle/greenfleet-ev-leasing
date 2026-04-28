package io.carlease.fleet_consulting.domain;

public class InvalidFleetAttributeException extends RuntimeException {

    public InvalidFleetAttributeException(String attributeName, String reason) {
        super("Invalid value for " + attributeName + ": " + reason);
    }
}
