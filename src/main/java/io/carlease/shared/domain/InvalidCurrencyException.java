package io.carlease.shared.domain;

public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String value) {
        super("Invalid currency: '" + value + "'. Supported values are: CHF, EUR, USD, GBP");
    }
}
