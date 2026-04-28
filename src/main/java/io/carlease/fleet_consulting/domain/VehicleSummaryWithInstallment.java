package io.carlease.fleet_consulting.domain;

public class VehicleSummaryWithInstallment {

    private final VehicleSummary vehicleSummary;
    private final Installment installment;

    private VehicleSummaryWithInstallment(VehicleSummary vehicleSummary, Installment installment) {
        this.vehicleSummary = vehicleSummary;
        this.installment = installment;
    }

    public static VehicleSummaryWithInstallment of(VehicleSummary vehicleSummary, Installment installment) {
        if (vehicleSummary == null)
            throw new InvalidFleetAttributeException("vehicleSummary", "must not be null");
        if (installment == null)
            throw new InvalidFleetAttributeException("installment", "must not be null");
        return new VehicleSummaryWithInstallment(vehicleSummary, installment);
    }

    public VehicleSummary getVehicleSummary() {
        return vehicleSummary;
    }

    public Installment getInstallment() {
        return installment;
    }
}
