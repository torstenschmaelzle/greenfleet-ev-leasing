package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.*;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class RecommendationRequest {

    private final UUID requestId;
    private final FleetRequirements requirements;
    private final List<VehicleRecommendation> recommendations;
    private final ScoringStrategy scoringStrategy;

    public RecommendationRequest(UUID requestId, FleetRequirements requirements, ScoringStrategy scoringStrategy) {
        if (requestId == null) throw new InvalidFleetAttributeException("requestId", "must not be null");
        if (requirements == null) throw new InvalidFleetAttributeException("requirements", "must not be null");
        if (scoringStrategy == null) throw new InvalidFleetAttributeException("scoringStrategy", "must not be null");
        this.requestId = requestId;
        this.requirements = requirements;
        this.scoringStrategy = scoringStrategy;
        this.recommendations = new ArrayList<>();
    }

    public void generateRecommendations(List<VehicleSummary> vehicles) {
        if (vehicles == null) throw new InvalidFleetAttributeException("vehicles", "must not be null");
        recommendations.clear();
        for (VehicleSummary vehicle : vehicles) {
            if (!vehicle.isAvailable()) continue;
            VehicleRecommendation recommendation = VehicleRecommendation.evaluate(
                    vehicle.getMonthlyLeasePrice(),
                    vehicle.getKwhPer100km(),
                    vehicle.getRangeKm(),
                    requirements,
                    scoringStrategy,
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getVehicleType()
            );
            recommendations.add(recommendation);
        }
    }

    public List<VehicleRecommendation> getTopRecommendations(int limit) {
        return recommendations.stream()
                .sorted(Comparator.comparingInt(r -> -r.getSuitabilityScore().getScore()))
                .limit(limit)
                .toList();
    }

    public VehicleRecommendation getBestMatch() {
        return recommendations.stream()
                .max(Comparator.comparingInt(r -> r.getSuitabilityScore().getScore()))
                .orElseThrow(() -> new InvalidFleetAttributeException("recommendations", "no recommendations available"));
    }

    public UUID getRequestId() {
        return requestId;
    }

    public FleetRequirements getRequirements() {
        return requirements;
    }

    public ScoringStrategy getScoringStrategy() {
        return scoringStrategy;
    }

    public List<VehicleRecommendation> getRecommendations() {
        return List.copyOf(recommendations);
    }
}
