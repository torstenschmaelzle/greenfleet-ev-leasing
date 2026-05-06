package io.carlease.sales.adapter.out.memory;

import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.domain.VehicleContract;
import io.carlease.sales.domain.ContractNumber;

import java.util.HashMap;
import java.util.Map;

public class ContractMemoryAdapter implements ContractPersistencePort {

	private final Map<ContractNumber, VehicleContract> repo;

	public ContractMemoryAdapter() {
		repo = new HashMap<>();
	}

	@Override
	public void save(VehicleContract contract) {
		repo.put(contract.number(), contract);
	}

	@Override
	public boolean exists(ContractNumber number) {
		return repo.containsKey(number);
	}

	@Override
	public VehicleContract with(ContractNumber number) {
		return repo.get(number);
	}

}
