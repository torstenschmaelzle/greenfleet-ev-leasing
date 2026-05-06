package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ContractMapper {

    public static ContractJpaEntity mapToJpaEntity(VehicleContract contract) {
        // leaseTerm/interest are null for fleet vehicle contracts (pre-calculated installment)
        return new ContractJpaEntity(
                contract.number().number(),
                contract.lessee().name(),
                contract.lessee().company(),
                contract.lessee().email(),
                contract.car().car(),
                contract.price().amountInCents(),
                contract.price().currency().name(),
                contract.isCalculated() && contract.leaseTerm() != null ? contract.leaseTerm().noOfMonths() : null,
                contract.isCalculated() && contract.interest() != null ? contract.interest().perYear() : null,
                contract.isApproved() ? contract.approvalDate().date() : null,
                contract.isSigned() ? contract.signatureDate().date() : null,
                contract.isCalculated() ? contract.downPayment().amount() : null,
                contract.isCalculated() ? contract.residualValue().amount() : null,
                contract.isCalculated() ? contract.installment().amountInCents() : null);
    }

    public static VehicleContract mapToVehicleContract(ContractJpaEntity e) {
        Currency currency = Currency.valueOf(e.getPriceCurrency());
        Customer lessee = Customer.of(
                e.getLessee(),
                e.getLesseeCompany() != null ? e.getLesseeCompany() : "",
                e.getLesseeEmail()   != null ? e.getLesseeEmail()   : "");

        // Fleet vehicle contract: has pre-calculated installment but no leaseTerm/interest
        if (e.getMonthlyInstallmentCents() != null && e.getLeaseTermInMonths() == null) {
            return ContractFactory.restoreFleetVehicleContract(
                    ContractNumber.of(e.getNumber()),
                    lessee,
                    Car.of(e.getCar()),
                    Amount.ofCents(e.getPriceAmount(), currency),
                    Amount.ofCents(e.getMonthlyInstallmentCents(), currency),
                    e.getApprovalDate() != null ? Optional.of(SignDate.of(e.getApprovalDate())) : Optional.empty(),
                    e.getSignatureDate() != null ? Optional.of(SignDate.of(e.getSignatureDate())) : Optional.empty());
        }

        // Regular contract: recalculate installment from leaseTerm + interest
        return ContractFactory.restoreVehicleContract(
                ContractNumber.of(e.getNumber()),
                lessee,
                Car.of(e.getCar()),
                Amount.ofCents(e.getPriceAmount(), currency),
                e.getLeaseTermInMonths() != null ? Optional.of(LeaseTerm.ofMonths(e.getLeaseTermInMonths())) : Optional.empty(),
                e.getInterestPerYear()   != null ? Optional.of(Interest.of(e.getInterestPerYear()))           : Optional.empty(),
                e.getApprovalDate()      != null ? Optional.of(SignDate.of(e.getApprovalDate()))              : Optional.empty(),
                e.getSignatureDate()     != null ? Optional.of(SignDate.of(e.getSignatureDate()))             : Optional.empty(),
                e.getDownPayment()       != null ? Amount.of(e.getDownPayment(), currency)                    : Amount.ofCents(0, currency),
                e.getResidualValue()     != null ? Amount.of(e.getResidualValue(), currency)                  : Amount.ofCents(0, currency));
    }
}
