package io.carlease.fleet_consulting.adapter.in.web;

import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fleet-consulting")
public class FleetConsultingController {

    private final GetRecommendationUseCase getRecommendationUseCase;

    public FleetConsultingController(GetRecommendationUseCase getRecommendationUseCase) {
        this.getRecommendationUseCase = getRecommendationUseCase;
    }

    @GetMapping("/recommendations")
    public List<VehicleRecommendation> getRecommendations(
            @RequestParam(name = "daily_route_km") int dailyRouteKm,
            @RequestParam(name = "number_of_drivers") int numberOfDrivers,
            @RequestParam(name = "charging_type") String chargingType,
            @RequestParam(name = "electricity_rate_cents") long electricityRateCents,
            @RequestParam(name = "currency") String currency,
            @RequestParam(name = "budget_monthly_max_cents") long budgetMonthlyMaxCents,
            @RequestParam(name = "size_preference", required = false) String sizePreference,
            @RequestParam(name = "range_weight", defaultValue = "40") int rangeWeight,
            @RequestParam(name = "cost_weight", defaultValue = "40") int costWeight,
            @RequestParam(name = "co2_weight", defaultValue = "20") int co2Weight) {

        Currency cur = Currency.valueOf(currency);

        FleetRequirements requirements = FleetRequirements.of(
                Kilometre.of(dailyRouteKm),
                NumberOfDrivers.of(numberOfDrivers),
                ChargingSetup.of(
                        ChargingType.valueOf(chargingType),
                        ElectricityRate.of(Amount.ofCents(electricityRateCents, cur))
                ),
                Budget.of(Amount.ofCents(budgetMonthlyMaxCents, cur)),
                sizePreference != null ? VehicleType.valueOf(sizePreference) : null,
                ScoringWeights.of(rangeWeight, costWeight, co2Weight)
        );

        return getRecommendationUseCase.recommend(requirements);
    }
}
