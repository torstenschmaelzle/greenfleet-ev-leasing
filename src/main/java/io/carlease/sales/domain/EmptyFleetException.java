package io.carlease.sales.domain;

public class EmptyFleetException extends ContractDomainException {
    public EmptyFleetException() {
        super("A fleet contract must contain at least one vehicle");
    }
}
