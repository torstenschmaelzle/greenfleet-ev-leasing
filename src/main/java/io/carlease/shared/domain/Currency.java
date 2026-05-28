package io.carlease.shared.domain;

public enum Currency {
    CHF, EUR, USD, GBP;

    public static Currency from(String value) {
        try {
            return Currency.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyException(value);
        }
    }
}
