package io.carlease.sales.application.port.in;

import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.SignDate;

public interface SignContractUseCase {

    void with(ContractNumber number, SignDate signatureDate);
}