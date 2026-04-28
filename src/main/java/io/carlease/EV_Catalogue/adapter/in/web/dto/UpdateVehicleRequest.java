package io.carlease.EV_Catalogue.adapter.in.web.dto;

public record UpdateVehicleRequest(
        String brand,
        String model,
        int rangeKm,
        String vehicleType,
        double consumptionKwhPer100km,
        double capacityKwh,
        double voltageV,
        double maxChargeSpeedKw,
        long monthlyLeasePriceCents,
        String currency,
        double newStateOfHealth,
        int newMeasuredRangeKm
) {}
