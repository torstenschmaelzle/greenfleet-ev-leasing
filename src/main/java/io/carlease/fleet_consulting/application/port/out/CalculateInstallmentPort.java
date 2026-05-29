package io.carlease.fleet_consulting.application.port.out;

import io.carlease.fleet_consulting.domain.Installment;
import io.carlease.shared.domain.LeaseTerm;
import io.carlease.shared.domain.Amount;

public interface CalculateInstallmentPort {
    Installment calculateInstallment(Amount basePrice, LeaseTerm leaseTerm);
}
