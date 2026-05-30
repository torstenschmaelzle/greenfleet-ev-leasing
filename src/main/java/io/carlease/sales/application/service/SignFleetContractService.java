package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.SignFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.SignDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignFleetContractService implements SignFleetContractUseCase {

    private final FleetContractPersistencePort fleetContractPersistencePort;
    private final PlaceVehicleUnderContractPort placeVehicleUnderContractPort;

    public SignFleetContractService(FleetContractPersistencePort fleetContractPersistencePort,
                                    PlaceVehicleUnderContractPort placeVehicleUnderContractPort) {
        this.fleetContractPersistencePort = fleetContractPersistencePort;
        this.placeVehicleUnderContractPort = placeVehicleUnderContractPort;
    }

    @Override
    @Transactional
    public void sign(ContractNumber number, SignDate date) {
        FleetContract contract = fleetContractPersistencePort.with(number);
        contract.sign(date);
        fleetContractPersistencePort.save(contract);

        contract.vehicleContracts().forEach(vc ->
                placeVehicleUnderContractPort.placeUnderContract(vc.vehicle().vin()));
    }
}
