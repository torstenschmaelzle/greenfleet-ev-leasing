package io.carlease.fleet_consulting.adapter.out;

import io.carlease.fleet_consulting.application.port.out.CalculateInstallmentPort;
import io.carlease.fleet_consulting.domain.DomainConstants;
import io.carlease.fleet_consulting.domain.FinancialCalculator;
import io.carlease.fleet_consulting.domain.Installment;
import io.carlease.fleet_consulting.domain.LeaseTerm;
import io.carlease.shared.domain.Amount;
import org.springframework.stereotype.Component;

@Component
public class InstallmentCalculatorAdapter implements CalculateInstallmentPort {

    @Override
    public Installment calculateInstallment(Amount basePrice, LeaseTerm leaseTerm) {
        double monthlyInterestPercent = DomainConstants.DEFAULT_INTEREST_RATE_PER_YEAR / 12.0;
        double pv = -1.0 * basePrice.getAmountInCents();

        double monthlyPaymentCents = Math.abs(
                FinancialCalculator.pmt(leaseTerm.getMonths(), monthlyInterestPercent, pv, 0, 0)
        );

        long roundedCents = Math.round(monthlyPaymentCents);
        return Installment.of(Amount.ofCents(roundedCents, basePrice.getCurrency()));
    }
}
