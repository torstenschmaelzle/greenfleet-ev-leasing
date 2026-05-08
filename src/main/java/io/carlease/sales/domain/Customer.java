package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Customer(String name, String company, String email) {

    /** Full constructor for fleet contracts where all details are known. */
    public static Customer of(String name, String company, String email) {
        if (!isValidName(name))
            throw new InvalidCustomerAttributeException("name", "must contain only letters and spaces, got: '" + name + "'");
        if (!isValidCompany(company))
            throw new InvalidCustomerAttributeException("company", "must not be blank and contain only letters, digits, spaces or & . , ' -");
        if (!isValidEmail(email))
            throw new InvalidCustomerAttributeException("email", "must be a valid email address, got: '" + email + "'");
        return new Customer(name, company, email);
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches("^\\p{L}+(\\s\\p{L}+)*$");
    }

    public static boolean isValidCompany(String company) {
        return company != null && !company.isBlank() && company.matches("^[\\p{L}0-9\\s&.,'\\.\\-]+$");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean isValid(String nameString) {
        return isValidName(nameString);
    }
}
