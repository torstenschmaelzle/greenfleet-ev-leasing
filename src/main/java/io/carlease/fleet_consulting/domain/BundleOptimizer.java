package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.BundleType;
import org.jmolecules.ddd.annotation.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BundleOptimizer {

    public Optional<FleetBundle> optimize(BundleType bundleType,
                                           List<ScoredVehicle> scoredVehicles,
                                           FleetRequirements requirements) {
        if (scoredVehicles == null || scoredVehicles.isEmpty()) return Optional.empty();

        int numberOfVehicles = requirements.getNumberOfVehicles().getValue();

        List<ScoredVehicle> sorted = scoredVehicles.stream()
                .sorted(Comparator.comparingDouble(v -> -v.getScore().getFinalScore()))
                .collect(Collectors.toList());

        if (sorted.size() < numberOfVehicles) return Optional.empty();

        List<ScoredVehicle> selected = new ArrayList<>(sorted.subList(0, numberOfVehicles));

        if (requirements.getBudget().isPresent()) {
            Budget budget = requirements.getBudget().get();
            List<ScoredVehicle> remaining = new ArrayList<>(sorted.subList(numberOfVehicles, sorted.size()));

            while (exceedsBudget(selected, requirements, budget) && !remaining.isEmpty()) {
                ScoredVehicle mostExpensive = selected.stream()
                        .max(Comparator.comparingLong(
                                v -> v.getVehicle().getInstallment().getMonthlyAmount().getAmountInCents()))
                        .orElseThrow();
                selected.remove(mostExpensive);
                selected.add(remaining.remove(0));
            }

            if (exceedsBudget(selected, requirements, budget)) return Optional.empty();
        }

        return Optional.of(buildBundle(bundleType, selected, requirements));
    }

    private boolean exceedsBudget(List<ScoredVehicle> selected, FleetRequirements requirements, Budget budget) {
        long installmentTotal = selected.stream()
                .mapToLong(v -> v.getVehicle().getInstallment().getMonthlyAmount().getAmountInCents())
                .sum();

        double kWhMonthlyTotal = selected.stream()
                .mapToDouble(v -> {
                    int dailyKm = requirements.getDailyRouteKm().getValue();
                    double kwhPer100km = v.getVehicle().getVehicleSummary().getKwhPer100km();
                    return dailyKm * 30.0 * (kwhPer100km / 100.0);
                }).sum();

        long ratePerKwhCents = requirements.getChargingSetup().getElectricityRate().getRatePerKwh().getAmountInCents();
        long chargingCostCents = Math.round(kWhMonthlyTotal * ratePerKwhCents);

        long totalCents = installmentTotal + chargingCostCents;
        return totalCents > budget.getMonthlyMax().getAmountInCents();
    }

    private FleetBundle buildBundle(BundleType bundleType, List<ScoredVehicle> selected,
                                     FleetRequirements requirements) {
        double totalCo2 = 0.0;
        double totalFinalScore = 0.0;
        int vehicles = requirements.getNumberOfVehicles().getValue();

        List<BundleVehicle> bundleVehicles = new ArrayList<>();
        for (ScoredVehicle sv : selected) {
            VehicleSummary vs = sv.getVehicle().getVehicleSummary();
            double co2PerVehicle = Co2Impact.calculate(requirements, vs.getKwhPer100km())
                    .getAnnualSavingsKg().doubleValue() / vehicles;
            totalCo2 += co2PerVehicle;
            totalFinalScore += sv.getScore().getFinalScore();

            bundleVehicles.add(BundleVehicle.of(
                    vs.getVin(),
                    vs.getBrand(),
                    vs.getModel(),
                    vs.getVehicleType(),
                    vs.getRangeKm().getValue(),
                    sv.getVehicle().getInstallment(),
                    co2PerVehicle,
                    sv.getScore()
            ));
        }

        double bundleScore = totalFinalScore / selected.size();

        TotalCostOfOwnership tco = TotalCostOfOwnership.calculateForBundle(requirements, selected);

        return FleetBundle.of(bundleType, bundleVehicles, tco, totalCo2, bundleScore);
    }
}
