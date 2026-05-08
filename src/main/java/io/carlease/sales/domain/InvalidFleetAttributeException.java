package io.carlease.sales.domain;

public class InvalidFleetAttributeException extends ContractDomainException {
    public InvalidFleetAttributeException(String attributeName, String reason) {
        super("Invalid value for " + attributeName + ": " + reason);
    }
}
