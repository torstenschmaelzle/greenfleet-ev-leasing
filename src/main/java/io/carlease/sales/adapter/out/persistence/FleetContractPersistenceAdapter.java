package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;
import io.carlease.sales.domain.VehicleContract;
import io.carlease.sales.domain.ContractNumber;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FleetContractPersistenceAdapter implements FleetContractPersistencePort {

    private final FleetContractRepository fleetContractRepository;
    private final ContractPersistencePort contractPersistencePort;

    public FleetContractPersistenceAdapter(FleetContractRepository fleetContractRepository,
                                           ContractPersistencePort contractPersistencePort) {
        this.fleetContractRepository = fleetContractRepository;
        this.contractPersistencePort = contractPersistencePort;
    }

    @Override
    public void save(FleetContract fleetContract) {
        fleetContract.vehicleContracts().forEach(contractPersistencePort::save);
        fleetContractRepository.save(FleetContractMapper.mapToJpaEntity(fleetContract));
    }

    @Override
    public FleetContract with(FleetContractNumber number) {
        FleetContractJpaEntity entity = fleetContractRepository.findById(number.number())
                .orElseThrow(() -> new IllegalArgumentException("FleetContract not found: " + number.number()));

        List<VehicleContract> vehicleContracts = entity.getVehicleContractNumbers().stream()
                .map(n -> contractPersistencePort.with(ContractNumber.of(n)))
                .toList();

        return FleetContractMapper.mapToDomain(entity, vehicleContracts);
    }

    @Override
    public boolean exists(FleetContractNumber number) {
        return fleetContractRepository.existsById(number.number());
    }
}
