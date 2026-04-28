package io.carlease.fleet_consulting.adapter.in.web.dto;

import java.util.List;

public class FleetBundleResponse {

    private final String bundleType;
    private final List<BundleVehicleResponse> vehicles;
    private final TcoResponse tco;
    private final double totalCo2SavingsKgPerYear;
    private final double bundleScore;

    public FleetBundleResponse(String bundleType, List<BundleVehicleResponse> vehicles,
                                TcoResponse tco, double totalCo2SavingsKgPerYear, double bundleScore) {
        this.bundleType = bundleType;
        this.vehicles = vehicles;
        this.tco = tco;
        this.totalCo2SavingsKgPerYear = totalCo2SavingsKgPerYear;
        this.bundleScore = bundleScore;
    }

    public String getBundleType() { return bundleType; }
    public List<BundleVehicleResponse> getVehicles() { return vehicles; }
    public TcoResponse getTco() { return tco; }
    public double getTotalCo2SavingsKgPerYear() { return totalCo2SavingsKgPerYear; }
    public double getBundleScore() { return bundleScore; }
}
