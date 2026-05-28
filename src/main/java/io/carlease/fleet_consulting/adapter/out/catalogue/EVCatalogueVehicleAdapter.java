package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.ev_catalogue.domain.Vehicle;
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
        return VehicleSummary.of(
                vehicle.getVehicleId().getVin(),
                vehicle.getHealthAdjustedLeasePrice(),
                vehicle.getVehicleSpecification().getConsumptionKwhPer100km().getValue().doubleValue(),
                Kilometre.of(vehicle.getLatestMeasuredRangeKm().getValue()),
                vehicle.getVehicleSpecification().getBrand(),
                vehicle.getVehicleSpecification().getModel(),
                vehicle.getVehicleSpecification().getVehicleType(),
                vehicle.isAvailable()
        );
    }
}
