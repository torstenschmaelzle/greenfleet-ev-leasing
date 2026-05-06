package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.SignFleetContractUseCase;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.application.port.out.PlaceVehicleUnderContractPort;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;
import io.carlease.sales.domain.SignDate;
import org.springframework.stereotype.Service;

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
    public void sign(FleetContractNumber number, SignDate date) {
        FleetContract contract = fleetContractPersistencePort.with(number);
        contract.sign(date);
        fleetContractPersistencePort.save(contract);

        // Transition each vehicle from RESERVED → UNDER_CONTRACT in the EV Catalogue
        contract.vehicleContracts().forEach(vc ->
                placeVehicleUnderContractPort.placeUnderContract(vc.car().car()));
    }
}
