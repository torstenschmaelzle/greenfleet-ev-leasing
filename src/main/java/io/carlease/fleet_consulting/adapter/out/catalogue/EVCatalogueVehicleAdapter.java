package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.domain.Percentage;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.Kilometre;
import io.carlease.fleet_consulting.domain.VehicleSummary;
import io.carlease.shared.domain.Amount;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EVCatalogueVehicleAdapter implements GetVehiclesPort {

    private final ListCatalogueUseCase listCatalogueUseCase;

    public EVCatalogueVehicleAdapter(ListCatalogueUseCase listCatalogueUseCase) {
        this.listCatalogueUseCase = listCatalogueUseCase;
    }

    @Override
    public List<VehicleSummary> getVehicles() {
        return listCatalogueUseCase.listAll().stream()
                .map(this::toVehicleSummary)
                .toList();
    }

    private VehicleSummary toVehicleSummary(Vehicle vehicle) {
        Amount adjustedPrice = applyHealthDiscount(
                vehicle.getMonthlyLeasePrice(),
                vehicle.getLatestStateOfHealth()
        );
        return VehicleSummary.of(
                vehicle.getVehicleId().getVin(),
                adjustedPrice,
                vehicle.getVehicleSpecification().getConsumptionKwhPer100km().getValue().doubleValue(),
                Kilometre.of(vehicle.getLatestMeasuredRangeKm().getValue()),
                vehicle.getVehicleSpecification().getBrand(),
                vehicle.getVehicleSpecification().getModel(),
                vehicle.getVehicleSpecification().getVehicleType(),
                vehicle.isAvailable()
        );
    }

    //Applies a health-based discount to the base lease price
    private Amount applyHealthDiscount(Amount basePrice, Percentage soh) {
        double s = soh.getValue().doubleValue() / 100.0;
        double factor = 1.0 - Math.max(0.0, 0.95 - s);
        long adjustedCents = Math.round(basePrice.getAmountInCents() * factor);
        return Amount.ofCents(adjustedCents, basePrice.getCurrency());
    }
}
