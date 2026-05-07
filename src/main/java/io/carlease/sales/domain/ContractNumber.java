package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record ContractNumber(String number) {

	public static ContractNumber of(String number) {
		return new ContractNumber(number);
	}

	public static ContractNumber generate() {
		return new ContractNumber(UUID.randomUUID().toString());
	}

}
