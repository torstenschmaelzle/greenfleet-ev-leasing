package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record FleetContractNumber(String number) {

    public static FleetContractNumber of(String number) {
        assert number != null && !number.isBlank();
        return new FleetContractNumber(number);
    }
}
