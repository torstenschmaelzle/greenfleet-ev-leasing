package io.carlease.fleet_consulting.adapter.in.web.dto;

public class TcoResponse {

    private final long monthlyInstallmentPerVehicleCents;
    private final long monthlyLeaseCostCents;
    private final long monthlyChargingCostCents;
    private final long monthlyTotalCents;
    private final long annualTotalCents;
    private final String currency;

    public TcoResponse(long monthlyInstallmentPerVehicleCents, long monthlyLeaseCostCents,
                       long monthlyChargingCostCents, long monthlyTotalCents,
                       long annualTotalCents, String currency) {
        this.monthlyInstallmentPerVehicleCents = monthlyInstallmentPerVehicleCents;
        this.monthlyLeaseCostCents = monthlyLeaseCostCents;
        this.monthlyChargingCostCents = monthlyChargingCostCents;
        this.monthlyTotalCents = monthlyTotalCents;
        this.annualTotalCents = annualTotalCents;
        this.currency = currency;
    }

    public long getMonthlyInstallmentPerVehicleCents() { return monthlyInstallmentPerVehicleCents; }
    public long getMonthlyLeaseCostCents() { return monthlyLeaseCostCents; }
    public long getMonthlyChargingCostCents() { return monthlyChargingCostCents; }
    public long getMonthlyTotalCents() { return monthlyTotalCents; }
    public long getAnnualTotalCents() { return annualTotalCents; }
    public String getCurrency() { return currency; }
}
