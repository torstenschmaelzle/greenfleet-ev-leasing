package io.carlease.ev_catalogue.adapter.in.web.dto;

import io.carlease.ev_catalogue.domain.Vehicle;

public record VehicleDTO(
        String vin,
        String brand,
        String model,
        String vehicleType,
        int rangeKm,
        double consumptionKwhPer100km,
        double capacityKwh,
        double voltageV,
        double maxChargeSpeedKw,
        int horsePowerHp,
        double accelerationSec,
        int topSpeedKmh,
        long monthlyLeasePriceCents,
        String currency,
        String status,
        double stateOfHealth,
        int measuredRangeKm
) {
    public static VehicleDTO from(Vehicle v) {
        return new VehicleDTO(
                v.getVehicleId().getVin(),
                v.getVehicleSpecification().getBrand(),
                v.getVehicleSpecification().getModel(),
                v.getVehicleSpecification().getVehicleType().name(),
                v.getVehicleSpecification().getRangeKm().getValue(),
                v.getVehicleSpecification().getConsumptionKwhPer100km().getValue().doubleValue(),
                v.getBatterySpecification().getCapacityKwh().doubleValue(),
                v.getBatterySpecification().getVoltageV().doubleValue(),
                v.getBatterySpecification().getMaxChargeSpeedKw().doubleValue(),
                v.getPerformance().getHorsePowerHp(),
                v.getPerformance().getAccelerationSec().doubleValue(),
                v.getPerformance().getTopSpeedKmh(),
                v.getMonthlyLeasePrice().getAmountInCents(),
                v.getMonthlyLeasePrice().getCurrency().name(),
                v.getStatus().name(),
                v.getLatestStateOfHealth().getValue().doubleValue(),
                v.getLatestMeasuredRangeKm().getValue()
        );
    }
}
