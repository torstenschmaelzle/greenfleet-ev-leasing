package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.CalculateInstallmentUseCase;
import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CalculateInstallmentService implements CalculateInstallmentUseCase {

    private final ContractPersistencePort contractPersistencePort;

    public CalculateInstallmentService(ContractPersistencePort contractPersistencePort) {
        this.contractPersistencePort = contractPersistencePort;
    }

    @Override
    public void calculateInstallment(
            String contractNumber,
            int leaseTerm,
            double interest,
            double downPayment,
            double residualValue

    ) {
        ContractNumber number = new ContractNumber(contractNumber);

        if (!contractPersistencePort.exists(number)) {
            throw new IllegalArgumentException("Contract does not exist: " + contractNumber);
        }

        VehicleContract contract = contractPersistencePort.with(number);

        contract.calculateInstallmentFor(
                new LeaseTerm(leaseTerm),
                new Interest(interest),
                Amount.of(downPayment, contract.price().currency()),
                Amount.of(residualValue, contract.price().currency())
        );

        contractPersistencePort.save(contract);
    }
}