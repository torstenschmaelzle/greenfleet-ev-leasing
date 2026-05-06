package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Richer customer identity for fleet contracts.
 * Single vehicle contracts use the simpler Customer value object.
 */
@ValueObject
public record FleetCustomer(String name, String company, String email) {

    public static FleetCustomer of(String name, String company, String email) {
        assert name != null && !name.isBlank();
        assert company != null && !company.isBlank();
        assert email != null && !email.isBlank();
        return new FleetCustomer(name, company, email);
    }
}
