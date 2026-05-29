package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.VehicleType;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        if (filtered.isEmpty()) {
            throw new NoVehiclesMatchingRequirementsException(describeReason(vehicles, requirements));
        }

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
        addPredefinedBundle(BundleType.PREMIUM, ScoringWeights.premium(), filtered, scorer, optimizer, requirements, bundles);

        List<FleetBundle> deduplicated = deduplicate(bundles);

        if (deduplicated.isEmpty()) {
            throw new NoVehiclesMatchingRequirementsException(describeBudgetOrCountReason(filtered, requirements));
        }

        return new FleetBundleRecommendation(UUID.randomUUID(), requirements, deduplicated);
    }

    private static void addPredefinedBundle(BundleType type, ScoringWeights weights,
                                             List<VehicleSummaryWithInstallment> vehicles,
                                             VehicleScorer scorer, BundleOptimizer optimizer,
                                             FleetRequirements requirements, List<FleetBundle> bundles) {
        List<ScoredVehicle> scored = scorer.score(vehicles, weights, requirements);
        optimizer.optimize(type, scored, requirements).ifPresent(bundles::add);
    }

    private static String describeReason(List<VehicleSummaryWithInstallment> vehicles,
                                          FleetRequirements requirements) {
        int requiredRange = (int) Math.ceil(
                requirements.getDailyRouteKm().getValue() * DomainConstants.RANGE_SAFETY_FACTOR);
        int maxAvailableRange = vehicles.stream()
                .mapToInt(v -> v.getVehicleSummary().getRangeKm().getValue())
                .max().orElse(0);

        if (maxAvailableRange < requiredRange) {
            return "daily route of " + requirements.getDailyRouteKm().getValue()
                    + " km requires a vehicle range of at least " + requiredRange
                    + " km, but the best available is " + maxAvailableRange + " km.";
        }

        if (requirements.getBrand().isPresent()) {
            String brand = requirements.getBrand().get();
            boolean brandExists = vehicles.stream()
                    .anyMatch(v -> v.getVehicleSummary().getBrand().equalsIgnoreCase(brand));
            if (!brandExists) {
                return "no vehicles of brand \"" + brand + "\" are available in the catalogue.";
            }
        }

        if (requirements.getVehicleType().isPresent()) {
            VehicleType type = requirements.getVehicleType().get();
            boolean typeExists = vehicles.stream()
                    .anyMatch(v -> v.getVehicleSummary().getVehicleType() == type);
            if (!typeExists) {
                return "no vehicles of type \"" + type + "\" are available in the catalogue.";
            }
        }

        return "the combination of your filters produced no results. Try adjusting the daily route, brand, or vehicle type.";
    }

    private static String describeBudgetOrCountReason(List<VehicleSummaryWithInstallment> filtered,
                                                       FleetRequirements requirements) {
        int requested = requirements.getNumberOfVehicles().getValue();

        if (filtered.size() < requested) {
            return "only " + filtered.size() + " vehicle(s) are available matching your criteria, but "
                    + requested + " were requested. Try reducing the number of vehicles.";
        }

        if (requirements.getBudget().isPresent()) {
            long budgetCents = requirements.getBudget().get().getMonthlyMax().getAmountInCents();
            int dailyKm = requirements.getDailyRouteKm().getValue();
            long ratePerKwhCents = requirements.getChargingSetup().getElectricityRate()
                    .getRatePerKwh().getAmountInCents();

            VehicleSummaryWithInstallment cheapest = filtered.stream()
                    .min(Comparator.comparingLong(v -> v.getInstallment().getMonthlyAmount().getAmountInCents()))
                    .orElseThrow();

            long cheapestInstallment = cheapest.getInstallment().getMonthlyAmount().getAmountInCents();
            double kwhPerMonth = dailyKm * 30.0 * (cheapest.getVehicleSummary().getKwhPer100km() / 100.0);
            long chargingCostCents = Math.round(kwhPerMonth * ratePerKwhCents);
            long cheapestTotalPerVehicle = cheapestInstallment + chargingCostCents;
            long cheapestTotalForFleet = cheapestTotalPerVehicle * requested;

            return "monthly budget of CHF " + String.format("%.0f", budgetCents / 100.0)
                    + " is too low. The cheapest fleet of " + requested + " vehicle(s) costs at least CHF "
                    + String.format("%.0f", cheapestTotalForFleet / 100.0) + "/month.";
        }

        return "the combination of your filters produced no results. Try adjusting your requirements.";
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

    public List<FleetBundle> getBundlesWithinBudget(Budget budget) {
        return bundles.stream()
                .filter(b -> b.isAffordableFor(budget))
                .collect(Collectors.toList());
    }

    public boolean hasCustomBundle() {
        return bundles.stream().anyMatch(b -> b.getBundleType() == BundleType.CUSTOM);
    }

    public Optional<FleetBundle> getBestBundle() {
        return bundles.stream().max(Comparator.comparingDouble(FleetBundle::getBundleScore));
    }
}
