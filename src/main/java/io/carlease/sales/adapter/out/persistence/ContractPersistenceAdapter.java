package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.domain.ContractNotFoundException;
import io.carlease.sales.domain.VehicleContract;
import io.carlease.sales.domain.ContractNumber;
import org.springframework.stereotype.Component;

@Component
public class ContractPersistenceAdapter implements ContractPersistencePort {

	private final ContractRepository repository;

	public ContractPersistenceAdapter(ContractRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(VehicleContract contract) {
		repository.save(ContractMapper.mapToJpaEntity(contract));
	}

	@Override
	public boolean exists(ContractNumber number) {
		return repository.findById(number.number()).isPresent();
	}

	@Override
	public VehicleContract with(ContractNumber number) {
		ContractJpaEntity contractJpaEntity = repository.findById(number.number())
				.orElseThrow(() -> new ContractNotFoundException(number.number()));
		return ContractMapper.mapToVehicleContract(contractJpaEntity);
	}

}
