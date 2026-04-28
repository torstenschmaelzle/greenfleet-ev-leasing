package io.carlease.fleet_consulting.application.service;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.*;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@ApplicationLayer
@Service
public class GetRecommendationService implements GetRecommendationUseCase {

    private final GetVehiclesPort getVehiclesPort;

    public GetRecommendationService(GetVehiclesPort getVehiclesPort) {
        this.getVehiclesPort = getVehiclesPort;
    }

    @Override
    public List<VehicleRecommendation> recommend(FleetRequirements requirements) {
        List<VehicleSummary> vehicles = getVehiclesPort.getVehicles();
        RecommendationRequest request = new RecommendationRequest(
                UUID.randomUUID(), requirements, new WeightedScoringStrategy());
        request.generateRecommendations(vehicles);
        return request.getTopRecommendations(Integer.MAX_VALUE);
    }
}
