package io.carlease.sales.adapter.out.catalogue;

import io.carlease.EV_Catalogue.application.port.in.releaseVehicle;
import io.carlease.EV_Catalogue.domain.VehicleId;
import io.carlease.sales.application.port.out.ReleaseVehiclePort;
import org.springframework.stereotype.Component;

@Component
public class EVCatalogueReleaseVehicleAdapter implements ReleaseVehiclePort {

    private final releaseVehicle releaseVehicleUseCase;

    public EVCatalogueReleaseVehicleAdapter(releaseVehicle releaseVehicleUseCase) {
        this.releaseVehicleUseCase = releaseVehicleUseCase;
    }

    @Override
    public void release(String vin) {
        releaseVehicleUseCase.release(VehicleId.of(vin));
    }
}
