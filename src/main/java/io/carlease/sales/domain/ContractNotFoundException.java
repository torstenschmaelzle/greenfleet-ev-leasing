package io.carlease.sales.domain;

public class ContractNotFoundException extends ContractDomainException {
    public ContractNotFoundException(String contractNumber) {
        super("Contract not found: " + contractNumber);
    }
}
