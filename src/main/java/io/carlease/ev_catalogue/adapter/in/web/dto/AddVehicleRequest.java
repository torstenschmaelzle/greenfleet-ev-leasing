package io.carlease.ev_catalogue.adapter.in.web.dto;

import jakarta.validation.constraints.*;

public record AddVehicleRequest(
        @NotBlank
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "vin must be a valid 17-character VIN")
        String vin,

        @NotBlank @Size(max = 50) String brand,
        @NotBlank @Size(max = 50) String model,

        @Min(1) int rangeKm,

        @NotBlank
        @Pattern(regexp = "^(SUV|SEDAN|VAN|COMPACT|ESTATE)$", message = "vehicleType must be one of: SUV, SEDAN, VAN, COMPACT, ESTATE")
        String vehicleType,

        @DecimalMin("0.1") double consumptionKwhPer100km,
        @DecimalMin("1.0") double capacityKwh,
        @DecimalMin("1.0") double voltageV,
        @DecimalMin("0.1") double maxChargeSpeedKw,

        @Min(1) int horsePowerHp,
        @DecimalMin("0.1") double accelerationSec,
        @Min(1) int topSpeedKmh,

        @Min(1) long monthlyLeasePriceCents,

        @NotBlank
        @Pattern(regexp = "^(CHF|EUR|USD|GBP)$", message = "currency must be one of: CHF, EUR, USD, GBP")
        String currency,

        @DecimalMin("0.0") @DecimalMax("100.0") double initialStateOfHealth,
        @Min(1) int initialMeasuredRangeKm
) {}
