package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class TotalCostOfOwnership {

    private final Amount monthlyLeaseCost;
    private final Amount monthlyChargingCost;
    private final Amount monthlyTotal;
    private final Amount annualTotal;

    private TotalCostOfOwnership(Amount monthlyLeaseCost, Amount monthlyChargingCost,
                                  Amount monthlyTotal, Amount annualTotal) {
        this.monthlyLeaseCost = monthlyLeaseCost;
        this.monthlyChargingCost = monthlyChargingCost;
        this.monthlyTotal = monthlyTotal;
        this.annualTotal = annualTotal;
    }

    public static TotalCostOfOwnership calculate(FleetRequirements req, Amount monthlyLeasePrice,
                                                   double kwhPer100km, Kilometre rangeKm) {
        if (req == null) throw new InvalidFleetAttributeException("req", "must not be null");
        if (monthlyLeasePrice == null) throw new InvalidFleetAttributeException("monthlyLeasePrice", "must not be null");

        int dailyKm = req.getDailyRouteKm().getValue();
        int drivers = req.getNumberOfDrivers().getValue();
        long ratePerKwhCents = req.getChargingSetup().getElectricityRate().getRatePerKwh().getAmountInCents();
        Currency rateCurrency = req.getChargingSetup().getElectricityRate().getRatePerKwh().getCurrency();

        double kWhConsumedMonthly = dailyKm * 30.0 * drivers * (kwhPer100km / 100.0);
        long chargingCostCents = Math.round(kWhConsumedMonthly * ratePerKwhCents);

        Amount monthlyChargingCost = Amount.ofCents(chargingCostCents, rateCurrency);
        Amount monthlyTotal = monthlyLeasePrice.add(monthlyChargingCost);
        Amount annualTotal = Amount.ofCents(monthlyTotal.getAmountInCents() * 12, monthlyTotal.getCurrency());

        return new TotalCostOfOwnership(monthlyLeasePrice, monthlyChargingCost, monthlyTotal, annualTotal);
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
        return monthlyLeaseCost.equals(other.monthlyLeaseCost)
                && monthlyChargingCost.equals(other.monthlyChargingCost)
                && monthlyTotal.equals(other.monthlyTotal)
                && annualTotal.equals(other.annualTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyLeaseCost, monthlyChargingCost, monthlyTotal, annualTotal);
    }
}
