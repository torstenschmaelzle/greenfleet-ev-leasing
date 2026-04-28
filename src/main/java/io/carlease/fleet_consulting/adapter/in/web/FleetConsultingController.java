package io.carlease.fleet_consulting.adapter.in.web;

import io.carlease.fleet_consulting.adapter.in.web.dto.*;
import io.carlease.fleet_consulting.application.port.in.GetRecommendationUseCase;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fleet-consulting")
public class FleetConsultingController {

    private final GetRecommendationUseCase getRecommendationUseCase;

    public FleetConsultingController(GetRecommendationUseCase getRecommendationUseCase) {
        this.getRecommendationUseCase = getRecommendationUseCase;
    }

    @GetMapping("/recommendations")
    public FleetBundleRecommendationResponse getRecommendations(
            @RequestParam(name = "daily_route_km") int dailyRouteKm,
            @RequestParam(name = "number_of_vehicles") int numberOfVehicles,
            @RequestParam(name = "lease_term_months") int leaseTermMonths,
            @RequestParam(name = "charging_type") String chargingType,
            @RequestParam(name = "electricity_rate_cents") long electricityRateCents,
            @RequestParam(name = "currency") String currency,
            @RequestParam(name = "budget_monthly_max_cents", required = false) Long budgetMonthlyMaxCents,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "vehicle_type", required = false) String vehicleType,
            @RequestParam(name = "range_weight", required = false) Integer rangeWeight,
            @RequestParam(name = "co2_weight", required = false) Integer co2Weight,
            @RequestParam(name = "cost_weight", required = false) Integer costWeight) {

        Currency cur = Currency.valueOf(currency);

        Optional<Budget> budget = budgetMonthlyMaxCents != null
                ? Optional.of(Budget.of(Amount.ofCents(budgetMonthlyMaxCents, cur)))
                : Optional.empty();

        Optional<String> brandFilter = Optional.ofNullable(brand);

        Optional<VehicleType> vehicleTypeFilter = vehicleType != null
                ? Optional.of(VehicleType.valueOf(vehicleType))
                : Optional.empty();

        FleetRequirements requirements = FleetRequirements.of(
                Kilometre.of(dailyRouteKm),
                NumberOfVehicles.of(numberOfVehicles),
                ChargingSetup.of(
                        ChargingType.valueOf(chargingType),
                        ElectricityRate.of(Amount.ofCents(electricityRateCents, cur))
                ),
                budget,
                LeaseTerm.of(leaseTermMonths),
                brandFilter,
                vehicleTypeFilter
        );

        Optional<ScoringWeights> customWeights = Optional.empty();
        if (rangeWeight != null && co2Weight != null && costWeight != null) {
            customWeights = Optional.of(ScoringWeights.of(rangeWeight, co2Weight, costWeight));
        }

        FleetBundleRecommendation recommendation = getRecommendationUseCase.recommend(requirements, customWeights);
        return toResponse(recommendation);
    }

    private FleetBundleRecommendationResponse toResponse(FleetBundleRecommendation recommendation) {
        List<FleetBundleResponse> bundleResponses = recommendation.getBundles().stream()
                .map(this::toBundleResponse)
                .collect(Collectors.toList());

        return new FleetBundleRecommendationResponse(
                recommendation.getRequestId(),
                recommendation.getRequirements().getNumberOfVehicles().getValue(),
                recommendation.getRequirements().getLeaseTerm().getMonths(),
                bundleResponses
        );
    }

    private FleetBundleResponse toBundleResponse(FleetBundle bundle) {
        List<BundleVehicleResponse> vehicleResponses = bundle.getVehicles().stream()
                .map(this::toVehicleResponse)
                .collect(Collectors.toList());

        TcoResponse tco = new TcoResponse(
                bundle.getTco().getMonthlyInstallmentPerVehicle().getAmountInCents(),
                bundle.getTco().getMonthlyLeaseCost().getAmountInCents(),
                bundle.getTco().getMonthlyChargingCost().getAmountInCents(),
                bundle.getTco().getMonthlyTotal().getAmountInCents(),
                bundle.getTco().getAnnualTotal().getAmountInCents(),
                bundle.getTco().getMonthlyTotal().getCurrency().name()
        );

        return new FleetBundleResponse(
                bundle.getBundleType().name(),
                vehicleResponses,
                tco,
                bundle.getTotalCo2SavingsKgPerYear(),
                bundle.getBundleScore()
        );
    }

    private BundleVehicleResponse toVehicleResponse(BundleVehicle vehicle) {
        VehicleScoreResponse score = new VehicleScoreResponse(
                vehicle.getScore().getRangeScore(),
                vehicle.getScore().getCo2Score(),
                vehicle.getScore().getCostScore(),
                vehicle.getScore().getFinalScore()
        );

        return new BundleVehicleResponse(
                vehicle.getVin(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getVehicleType().name(),
                vehicle.getRangeKm(),
                vehicle.getInstallment().getMonthlyAmount().getAmountInCents(),
                vehicle.getInstallment().getMonthlyAmount().getCurrency().name(),
                vehicle.getCo2SavingsKgPerYear(),
                score
        );
    }
}
