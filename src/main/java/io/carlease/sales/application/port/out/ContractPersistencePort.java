package io.carlease.sales.application.port.out;

import io.carlease.sales.domain.VehicleContract;
import io.carlease.sales.domain.ContractNumber;
import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface ContractPersistencePort {

	VehicleContract with(ContractNumber number);

	void save(VehicleContract contract);

	void delete(ContractNumber number);

}
