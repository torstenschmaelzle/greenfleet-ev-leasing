package io.carlease.fleet_consulting.application.port.in;

import io.carlease.fleet_consulting.domain.FleetBundleRecommendation;
import io.carlease.fleet_consulting.domain.FleetRequirements;
import io.carlease.fleet_consulting.domain.ScoringWeights;

import java.util.Optional;

public interface GetRecommendationUseCase {
    FleetBundleRecommendation recommend(FleetRequirements requirements, Optional<ScoringWeights> customWeights);
}
