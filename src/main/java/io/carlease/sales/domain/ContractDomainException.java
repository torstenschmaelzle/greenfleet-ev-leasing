package io.carlease.sales.domain;

public abstract class ContractDomainException extends RuntimeException {
    protected ContractDomainException(String message) {
        super(message);
    }
}
