package io.carlease.sales.domain;

public class InvalidCustomerAttributeException extends ContractDomainException {
    public InvalidCustomerAttributeException(String attributeName, String reason) {
        super("Invalid value for " + attributeName + ": " + reason);
    }
}
