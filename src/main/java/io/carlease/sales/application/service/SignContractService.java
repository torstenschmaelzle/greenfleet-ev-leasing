package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.SignContractUseCase;
import io.carlease.sales.application.port.out.ContractPersistencePort;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.SignDate;
import jakarta.transaction.Transactional;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.stereotype.Component;

@Component
@ApplicationLayer
@Service
@Transactional
public class SignContractService implements SignContractUseCase {

    private final ContractPersistencePort contractPersistencePort;

    public SignContractService(ContractPersistencePort contractPersistencePort) {
        this.contractPersistencePort = contractPersistencePort;
    }

    @Override
    public void with(ContractNumber number, SignDate signatureDate) {
        assert number != null;
        assert signatureDate != null;

        var contract = this.contractPersistencePort.with(number);

        contract.sign(signatureDate);

        this.contractPersistencePort.save(contract);
    }
}