package io.carlease.EV_Catalogue.adapter.in.web.dto;

public record AddVehicleRequest(
        String vin,
        String brand,
        String model,
        int rangeKm,
        String vehicleType,
        double consumptionKwhPer100km,
        double capacityKwh,
        double voltageV,
        double maxChargeSpeedKw,
        int horsePowerHp,
        double accelerationSec,
        int topSpeedKmh,
        long monthlyLeasePriceCents,
        String currency,
        double initialStateOfHealth,
        int initialMeasuredRangeKm
) {}
