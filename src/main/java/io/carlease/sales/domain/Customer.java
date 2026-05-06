package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Customer(String name, String company, String email) {

    /** Full constructor for fleet contracts where all details are known. */
    public static Customer of(String name, String company, String email) {
        assert isValidName(name);
        assert company != null;
        assert email != null;
        return new Customer(name, company, email);
    }

    /** Name-only constructor — used for single vehicle contracts and test data. */
    public static Customer of(String name) {
        assert isValidName(name);
        return new Customer(name, "", "");
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches("^\\p{L}+(\\s\\p{L}+)*$");
    }

    /** Kept for backwards compatibility with existing validation calls. */
    public static boolean isValid(String nameString) {
        return isValidName(nameString);
    }
}
