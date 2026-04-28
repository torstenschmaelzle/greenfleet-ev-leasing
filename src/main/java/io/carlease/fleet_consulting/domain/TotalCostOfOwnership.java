package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class TotalCostOfOwnership {

    private final Amount monthlyInstallmentPerVehicle;
    private final Amount monthlyLeaseCost;
    private final Amount monthlyChargingCost;
    private final Amount monthlyTotal;
    private final Amount annualTotal;

    private TotalCostOfOwnership(Amount monthlyInstallmentPerVehicle, Amount monthlyLeaseCost,
                                  Amount monthlyChargingCost, Amount monthlyTotal, Amount annualTotal) {
        this.monthlyInstallmentPerVehicle = monthlyInstallmentPerVehicle;
        this.monthlyLeaseCost = monthlyLeaseCost;
        this.monthlyChargingCost = monthlyChargingCost;
        this.monthlyTotal = monthlyTotal;
        this.annualTotal = annualTotal;
    }

    public static TotalCostOfOwnership calculate(FleetRequirements req, Installment installment,
                                                   double kwhPer100km) {
        if (req == null) throw new InvalidFleetAttributeException("req", "must not be null");
        if (installment == null) throw new InvalidFleetAttributeException("installment", "must not be null");

        int dailyKm = req.getDailyRouteKm().getValue();
        int vehicles = req.getNumberOfVehicles().getValue();
        long ratePerKwhCents = req.getChargingSetup().getElectricityRate().getRatePerKwh().getAmountInCents();
        Currency rateCurrency = req.getChargingSetup().getElectricityRate().getRatePerKwh().getCurrency();

        Amount installmentAmount = installment.getMonthlyAmount();
        long leaseCostCents = installmentAmount.getAmountInCents() * vehicles;
        Amount monthlyLeaseCost = Amount.ofCents(leaseCostCents, installmentAmount.getCurrency());

        double kWhConsumedMonthly = dailyKm * 30.0 * vehicles * (kwhPer100km / 100.0);
        long chargingCostCents = Math.round(kWhConsumedMonthly * ratePerKwhCents);
        Amount monthlyChargingCost = Amount.ofCents(chargingCostCents, rateCurrency);

        Amount monthlyTotal = monthlyLeaseCost.add(monthlyChargingCost);
        Amount annualTotal = Amount.ofCents(monthlyTotal.getAmountInCents() * 12, monthlyTotal.getCurrency());

        return new TotalCostOfOwnership(installmentAmount, monthlyLeaseCost, monthlyChargingCost, monthlyTotal, annualTotal);
    }

    public Amount getMonthlyInstallmentPerVehicle() {
        return monthlyInstallmentPerVehicle;
    }

    public Amount getMonthlyLeaseCost() {
        return monthlyLeaseCost;
    }

    public Amount getMonthlyChargingCost() {
        return monthlyChargingCost;
    }

    public Amount getMonthlyTotal() {
        return monthlyTotal;
    }

    public Amount getAnnualTotal() {
        return annualTotal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TotalCostOfOwnership other = (TotalCostOfOwnership) obj;
        return monthlyInstallmentPerVehicle.equals(other.monthlyInstallmentPerVehicle)
                && monthlyLeaseCost.equals(other.monthlyLeaseCost)
                && monthlyChargingCost.equals(other.monthlyChargingCost)
                && monthlyTotal.equals(other.monthlyTotal)
                && annualTotal.equals(other.annualTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyInstallmentPerVehicle, monthlyLeaseCost, monthlyChargingCost, monthlyTotal, annualTotal);
    }
}
