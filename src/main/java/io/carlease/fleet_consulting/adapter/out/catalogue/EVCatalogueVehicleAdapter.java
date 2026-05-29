package io.carlease.fleet_consulting.adapter.out.catalogue;

import io.carlease.ev_catalogue.application.port.in.CatalogueVehicleView;
import io.carlease.ev_catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.fleet_consulting.application.port.out.GetVehiclesPort;
import io.carlease.fleet_consulting.domain.Kilometre;
import io.carlease.fleet_consulting.domain.VehicleSummary;
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
        return listCatalogueUseCase.listSummaries().stream()
                .map(this::toVehicleSummary)
                .toList();
    }

    private VehicleSummary toVehicleSummary(CatalogueVehicleView view) {
        return VehicleSummary.of(
                view.vin(),
                view.healthAdjustedLeasePrice(),
                view.kwhPer100km(),
                Kilometre.of(view.latestMeasuredRangeKm()),
                view.brand(),
                view.model(),
                view.vehicleType(),
                view.available()
        );
    }
}
