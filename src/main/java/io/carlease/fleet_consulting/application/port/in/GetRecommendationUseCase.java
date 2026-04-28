package io.carlease.fleet_consulting.application.port.in;

import io.carlease.fleet_consulting.domain.FleetRequirements;
import io.carlease.fleet_consulting.domain.VehicleRecommendation;

import java.util.List;

public interface GetRecommendationUseCase {
    List<VehicleRecommendation> recommend(FleetRequirements requirements);
}
