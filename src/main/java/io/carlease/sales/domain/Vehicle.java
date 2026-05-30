package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public record Vehicle(String vin) {

	public static Vehicle of(String vin) {
		Objects.requireNonNull(vin, "vin must not be null");
		if (vin.isBlank()) throw new InvalidFleetAttributeException("vin", "must not be blank");
		return new Vehicle(vin);
	}

}
