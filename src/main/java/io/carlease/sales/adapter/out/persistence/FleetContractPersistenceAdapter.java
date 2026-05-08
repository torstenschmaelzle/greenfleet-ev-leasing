package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.ContractNotFoundException;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.VehicleContract;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public FleetContract with(ContractNumber number) {
        FleetContractJpaEntity entity = fleetContractRepository.findById(number.number())
                .orElseThrow(() -> new ContractNotFoundException(number.number()));

        List<VehicleContract> vehicleContracts = entity.getVehicleContractNumbers().stream()
                .map(n -> contractPersistencePort.with(ContractNumber.of(n)))
                .toList();

        return FleetContractMapper.mapToDomain(entity, vehicleContracts);
    }

    @Override
    public List<FleetContract> listAll() {
        List<FleetContractJpaEntity> entities = new ArrayList<>();
        fleetContractRepository.findAll().forEach(entities::add);
        return toFleetContracts(entities);
    }

    @Override
    public List<FleetContract> findByCustomerEmail(String email) {
        return toFleetContracts(fleetContractRepository.findByCustomerEmail(email));
    }

    @Override
    public void delete(ContractNumber number) {
        FleetContractJpaEntity entity = fleetContractRepository.findById(number.number())
                .orElseThrow(() -> new ContractNotFoundException(number.number()));
        entity.getVehicleContractNumbers()
                .forEach(n -> contractPersistencePort.delete(ContractNumber.of(n)));
        fleetContractRepository.deleteById(number.number());
    }

    private List<FleetContract> toFleetContracts(List<FleetContractJpaEntity> entities) {
        return entities.stream()
                .map(entity -> {
                    List<VehicleContract> vehicleContracts = entity.getVehicleContractNumbers().stream()
                            .map(n -> contractPersistencePort.with(ContractNumber.of(n)))
                            .toList();
                    return FleetContractMapper.mapToDomain(entity, vehicleContracts);
                })
                .toList();
    }

}
