package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.ContractNumber;

public interface DeleteFleetContractUseCase {
    void delete(ContractNumber number);
}
