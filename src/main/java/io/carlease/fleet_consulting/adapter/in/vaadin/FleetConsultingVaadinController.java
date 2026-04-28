package io.carlease.fleet_consulting.adapter.in.vaadin;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.domain.FleetRequirements;
import io.carlease.fleet_consulting.domain.VehicleRecommendation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FleetConsultingVaadinController {

    private final GetRecommendationUseCase getRecommendationUseCase;

    public FleetConsultingVaadinController(GetRecommendationUseCase getRecommendationUseCase) {
        this.getRecommendationUseCase = getRecommendationUseCase;
    }

    public List<VehicleRecommendation> recommend(FleetRequirements requirements) {
        return getRecommendationUseCase.recommend(requirements);
    }
}
