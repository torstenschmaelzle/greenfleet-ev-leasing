package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleScorer {

    public List<ScoredVehicle> score(List<VehicleSummaryWithInstallment> vehicles,
                                      ScoringWeights weights,
                                      FleetRequirements requirements) {
        if (vehicles == null || vehicles.isEmpty()) return List.of();

        int requiredKm = (int) Math.ceil(requirements.getDailyRouteKm().getValue() * DomainConstants.RANGE_SAFETY_FACTOR);

        double maxRangeBuffer = vehicles.stream()
                .mapToDouble(v -> v.getVehicleSummary().getRangeKm().getValue() - requiredKm)
                .max().orElse(1.0);

        double maxCo2 = vehicles.stream()
                .mapToDouble(v -> co2Savings(v, requirements))
                .max().orElse(1.0);

        double maxInstallment = vehicles.stream()
                .mapToDouble(v -> v.getInstallment().getMonthlyAmount().getAmountInCents())
                .max().orElse(1.0);

        if (maxRangeBuffer <= 0) maxRangeBuffer = 1.0;
        if (maxCo2 <= 0) maxCo2 = 1.0;
        if (maxInstallment <= 0) maxInstallment = 1.0;

        final double finalMaxRangeBuffer = maxRangeBuffer;
        final double finalMaxCo2 = maxCo2;
        final double finalMaxInstallment = maxInstallment;

        return vehicles.stream().map(v -> {
            double rangeBuffer = v.getVehicleSummary().getRangeKm().getValue() - requiredKm;
            double rangeScore = rangeBuffer / finalMaxRangeBuffer;

            double co2 = co2Savings(v, requirements);
            double co2Score = co2 / finalMaxCo2;

            double installmentCents = v.getInstallment().getMonthlyAmount().getAmountInCents();
            double costScore = 1.0 - (installmentCents / finalMaxInstallment);

            rangeScore = Math.max(0.0, Math.min(1.0, rangeScore));
            co2Score = Math.max(0.0, Math.min(1.0, co2Score));
            costScore = Math.max(0.0, Math.min(1.0, costScore));

            IndividualVehicleScore individualScore = IndividualVehicleScore.of(rangeScore, co2Score, costScore, weights);
            return ScoredVehicle.of(v, individualScore);
        }).collect(Collectors.toList());
    }

    private double co2Savings(VehicleSummaryWithInstallment v, FleetRequirements requirements) {
        Co2Impact impact = Co2Impact.calculate(requirements, v.getVehicleSummary().getKwhPer100km());
        return impact.getAnnualSavingsKg().doubleValue();
    }
}
