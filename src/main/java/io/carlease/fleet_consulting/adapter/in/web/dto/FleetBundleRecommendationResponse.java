package io.carlease.fleet_consulting.adapter.in.web.dto;

import java.util.List;
import java.util.UUID;

public class FleetBundleRecommendationResponse {

    private final UUID requestId;
    private final int numberOfVehicles;
    private final int leaseTermMonths;
    private final List<FleetBundleResponse> bundles;

    public FleetBundleRecommendationResponse(UUID requestId, int numberOfVehicles,
                                              int leaseTermMonths, List<FleetBundleResponse> bundles) {
        this.requestId = requestId;
        this.numberOfVehicles = numberOfVehicles;
        this.leaseTermMonths = leaseTermMonths;
        this.bundles = bundles;
    }

    public UUID getRequestId() { return requestId; }
    public int getNumberOfVehicles() { return numberOfVehicles; }
    public int getLeaseTermMonths() { return leaseTermMonths; }
    public List<FleetBundleResponse> getBundles() { return bundles; }
}
