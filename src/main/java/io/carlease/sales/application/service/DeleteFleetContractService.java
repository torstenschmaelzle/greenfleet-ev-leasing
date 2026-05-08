package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.DeleteFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.ContractAlreadySignedException;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.FleetContract;
import org.springframework.stereotype.Service;

@Service
public class DeleteFleetContractService implements DeleteFleetContractUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;

    public DeleteFleetContractService(FleetContractPersistencePort fleetContractPersistencePort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
    }

    @Override
    public void delete(ContractNumber number) {
        FleetContract contract = fleetContractPersistencePort.with(number);
        if (contract.isSigned()) throw new ContractAlreadySignedException(number.number());
        fleetContractPersistencePort.delete(number);
    }
}
