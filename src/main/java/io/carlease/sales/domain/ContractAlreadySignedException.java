package io.carlease.sales.domain;

public class ContractAlreadySignedException extends ContractDomainException {
    public ContractAlreadySignedException(String contractNumber) {
        super("Contract " + contractNumber + " has already been signed");
    }
}
