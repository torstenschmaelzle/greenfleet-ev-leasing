package io.carlease.ev_catalogue.application.port.in;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.VehicleType;

public record CatalogueVehicleView(
        String vin,
        Amount healthAdjustedLeasePrice,
        double kwhPer100km,
        int latestMeasuredRangeKm,
        String brand,
        String model,
        VehicleType vehicleType,
        boolean available
) {}
