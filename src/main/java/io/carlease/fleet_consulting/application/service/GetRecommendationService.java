package io.carlease.fleet_consulting.application.service;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.application.port.out.CalculateInstallmentPort;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ApplicationLayer
@Service
public class GetRecommendationService implements GetRecommendationUseCase {

    private final GetVehiclesPort getVehiclesPort;
    private final CalculateInstallmentPort calculateInstallmentPort;

    public GetRecommendationService(GetVehiclesPort getVehiclesPort,
                                     CalculateInstallmentPort calculateInstallmentPort) {
        this.getVehiclesPort = getVehiclesPort;
        this.calculateInstallmentPort = calculateInstallmentPort;
    }

    @Override
    public FleetBundleRecommendation recommend(FleetRequirements requirements,
                                                Optional<ScoringWeights> customWeights) {
        List<VehicleSummary> vehicles = getVehiclesPort.getVehicles();

        List<VehicleSummaryWithInstallment> vehiclesWithInstallment = vehicles.stream()
                .map(v -> {
                    Installment installment = calculateInstallmentPort.calculateInstallment(
                            v.getBasePrice(), requirements.getLeaseTerm());
                    return VehicleSummaryWithInstallment.of(v, installment);
                })
                .collect(Collectors.toList());

        return FleetBundleRecommendation.generate(requirements, vehiclesWithInstallment, customWeights);
    }
}
