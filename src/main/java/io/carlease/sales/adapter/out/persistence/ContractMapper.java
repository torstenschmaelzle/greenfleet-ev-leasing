package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ContractMapper {

    public static ContractJpaEntity mapToJpaEntity(VehicleContract contract) {
        return new ContractJpaEntity(
                contract.number().number(),
                contract.lessee().name(),
                contract.lessee().company(),
                contract.lessee().email(),
                contract.vehicle().vin(),
                contract.price().getAmountInCents(),
                contract.price().getCurrency().name(),
                null,
                null,
                contract.isSigned() ? contract.signatureDate().date() : null,
                null,
                null,
                contract.installment().getAmountInCents());
    }

    public static VehicleContract mapToVehicleContract(ContractJpaEntity e) {
        Currency currency = Currency.valueOf(e.getPriceCurrency());
        Customer customer = Customer.of(e.getLessee(), e.getLesseeCompany(), e.getLesseeEmail());
        return ContractFactory.restoreFleetVehicleContract(
                ContractNumber.of(e.getNumber()),
                customer,
                Vehicle.of(e.getCar()),
                Amount.ofCents(e.getPriceAmount(), currency),
                Amount.ofCents(e.getMonthlyInstallmentCents(), currency),
                e.getSignatureDate() != null ? Optional.of(SignDate.of(e.getSignatureDate())) : Optional.empty());
    }
}
