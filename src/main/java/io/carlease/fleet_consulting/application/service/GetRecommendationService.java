package io.carlease.fleet_consulting.application.service;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.LeaseTerm;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ApplicationLayer
@Service
public class GetRecommendationService implements GetRecommendationUseCase {

    private final GetVehiclesPort getVehiclesPort;
    private final Logger logger = LoggerFactory.getLogger(GetRecommendationService.class);

    public GetRecommendationService(GetVehiclesPort getVehiclesPort) {
        this.getVehiclesPort = getVehiclesPort;
    }

    @Override
    public FleetBundleRecommendation recommend(FleetRequirements requirements,
                                                Optional<ScoringWeights> customWeights) {
        logger.info("Generating fleet recommendation: {} vehicles, {} km/day, {} months",
                requirements.getNumberOfVehicles().getValue(),
                requirements.getDailyRouteKm().getValue(),
                requirements.getLeaseTerm().getMonths());

        List<VehicleSummary> vehicles = getVehiclesPort.getVehicles();

        List<VehicleSummaryWithInstallment> vehiclesWithInstallment = vehicles.stream()
                .map(v -> {
                    Installment installment = FinancialCalculator.calculateInstallment(
                            v.getBasePrice(), requirements.getLeaseTerm());
                    return VehicleSummaryWithInstallment.of(v, installment);
                })
                .collect(Collectors.toList());

        FleetBundleRecommendation result = FleetBundleRecommendation.generate(requirements, vehiclesWithInstallment, customWeights);
        logger.info("Fleet recommendation generated: {} bundles", result.getBundles().size());
        return result;
    }
}
