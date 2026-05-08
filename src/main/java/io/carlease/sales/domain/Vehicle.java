package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Vehicle(String vin) {

	public static Vehicle of(String vin) {
		return new Vehicle(vin);
	}

}
