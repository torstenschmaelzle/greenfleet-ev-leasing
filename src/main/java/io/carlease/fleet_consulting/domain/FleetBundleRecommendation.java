package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.VehicleType;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AggregateRoot
public class FleetBundleRecommendation {

    private final UUID requestId;
    private final FleetRequirements requirements;
    private final List<FleetBundle> bundles;

    private FleetBundleRecommendation(UUID requestId, FleetRequirements requirements, List<FleetBundle> bundles) {
        this.requestId = requestId;
        this.requirements = requirements;
        this.bundles = bundles;
    }

    public static FleetBundleRecommendation generate(
            FleetRequirements requirements,
            List<VehicleSummaryWithInstallment> vehicles,
            Optional<ScoringWeights> customWeights) {

        if (requirements == null)
            throw new InvalidFleetAttributeException("requirements", "must not be null");
        if (vehicles == null)
            throw new InvalidFleetAttributeException("vehicles", "must not be null");
        if (customWeights == null)
            throw new InvalidFleetAttributeException("customWeights", "must not be null");

        List<VehicleSummaryWithInstallment> filtered = applyHardFilters(vehicles, requirements);

        VehicleScorer scorer = new VehicleScorer();
        BundleOptimizer optimizer = new BundleOptimizer();

        List<FleetBundle> bundles = new ArrayList<>();

        if (customWeights.isPresent()) {
            List<ScoredVehicle> scored = scorer.score(filtered, customWeights.get(), requirements);
            optimizer.optimize(BundleType.CUSTOM, scored, requirements)
                    .ifPresent(bundles::add);
        }

        addPredefinedBundle(BundleType.BALANCED, ScoringWeights.balanced(), filtered, scorer, optimizer, requirements, bundles);
        addPredefinedBundle(BundleType.ECO, ScoringWeights.eco(), filtered, scorer, optimizer, requirements, bundles);
        addPredefinedBundle(BundleType.ECONOMIC, ScoringWeights.economic(), filtered, scorer, optimizer, requirements, bundles);

        List<FleetBundle> deduplicated = deduplicate(bundles);

        return new FleetBundleRecommendation(UUID.randomUUID(), requirements, deduplicated);
    }

    private static void addPredefinedBundle(BundleType type, ScoringWeights weights,
                                             List<VehicleSummaryWithInstallment> vehicles,
                                             VehicleScorer scorer, BundleOptimizer optimizer,
                                             FleetRequirements requirements, List<FleetBundle> bundles) {
        List<ScoredVehicle> scored = scorer.score(vehicles, weights, requirements);
        optimizer.optimize(type, scored, requirements).ifPresent(bundles::add);
    }

    private static List<VehicleSummaryWithInstallment> applyHardFilters(
            List<VehicleSummaryWithInstallment> vehicles, FleetRequirements requirements) {

        return vehicles.stream()
                .filter(v -> v.getVehicleSummary().isAvailable())
                .filter(v -> requirements.getBrand()
                        .map(b -> b.equalsIgnoreCase(v.getVehicleSummary().getBrand()))
                        .orElse(true))
                .filter(v -> requirements.getVehicleType()
                        .map(t -> t == v.getVehicleSummary().getVehicleType())
                        .orElse(true))
                .filter(v -> {
                    RangeFit fit = RangeFit.evaluate(requirements.getDailyRouteKm(), v.getVehicleSummary().getRangeKm());
                    return fit.isSufficient();
                })
                .collect(Collectors.toList());
    }

    private static List<FleetBundle> deduplicate(List<FleetBundle> bundles) {
        List<FleetBundle> result = new ArrayList<>();
        for (FleetBundle candidate : bundles) {
            boolean duplicate = result.stream().anyMatch(existing -> sameVins(existing, candidate));
            if (!duplicate) {
                result.add(candidate);
            }
        }
        return result;
    }

    private static boolean sameVins(FleetBundle a, FleetBundle b) {
        List<String> vinsA = a.getVehicles().stream()
                .map(BundleVehicle::getVin)
                .sorted()
                .collect(Collectors.toList());
        List<String> vinsB = b.getVehicles().stream()
                .map(BundleVehicle::getVin)
                .sorted()
                .collect(Collectors.toList());
        return vinsA.equals(vinsB);
    }

    public UUID getRequestId() {
        return requestId;
    }

    public FleetRequirements getRequirements() {
        return requirements;
    }

    public List<FleetBundle> getBundles() {
        return bundles;
    }
}
