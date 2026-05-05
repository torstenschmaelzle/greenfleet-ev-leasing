package io.carlease.EV_Catalogue.adapter.out.persistence;

import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    public static VehicleJpaEntity mapToJpaEntity(Vehicle vehicle) {
        List<BatteryHealthRecordEmbeddable> healthRecords = vehicle.getHealthMeasurements().stream()
                .map(record -> new BatteryHealthRecordEmbeddable(
                        record.getMeasurementId().toString(),
                        record.getMeasuredAt(),
                        record.getStateOfHealth().getValue(),
                        record.getMeasuredRangeKm().getValue()
                ))
                .collect(Collectors.toList());

        ReservationLock lock = vehicle.getLock().orElse(null);

        return new VehicleJpaEntity(
                vehicle.getVehicleId().getVin(),
                vehicle.getVehicleSpecification().getBrand(),
                vehicle.getVehicleSpecification().getModel(),
                vehicle.getVehicleSpecification().getRangeKm().getValue(),
                vehicle.getVehicleSpecification().getVehicleType().name(),
                vehicle.getVehicleSpecification().getConsumptionKwhPer100km().getValue(),
                vehicle.getBatterySpecification().getCapacityKwh(),
                vehicle.getBatterySpecification().getVoltageV(),
                vehicle.getBatterySpecification().getMaxChargeSpeedKw(),
                vehicle.getPerformance().getHorsePowerHp(),
                vehicle.getPerformance().getAccelerationSec(),
                vehicle.getPerformance().getTopSpeedKmh(),
                vehicle.getMonthlyLeasePrice().getAmountInCents(),
                vehicle.getMonthlyLeasePrice().getCurrency().name(),
                vehicle.getStatus().name(),
                lock != null ? lock.getLockId().toString() : null,
                lock != null ? lock.getLockedAt() : null,
                lock != null ? lock.getExpiresAt() : null,
                lock != null ? lock.getDuration().getHours() : null,
                healthRecords
        );
    }

    public static Vehicle mapToDomain(VehicleJpaEntity entity) {
        List<BatteryHealthRecordEmbeddable> records = entity.getHealthRecords();
        if (records == null || records.isEmpty()) {
            throw new InvalidVehicleAttributeException("healthMeasurements",
                    "no battery health records found for vehicle: " + entity.getVin());
        }
        BatteryHealthRecordEmbeddable first = records.get(0);

        Vehicle vehicle = Vehicle.create(
                VehicleId.of(entity.getVin()),
                VehicleSpecification.of(
                        entity.getBrand(),
                        entity.getModel(),
                        Range.ofKm(entity.getRangeKm()),
                        VehicleType.valueOf(entity.getVehicleType()),
                        EnergyConsumption.of(entity.getConsumptionKwhPer100km())
                ),
                BatterySpecification.of(
                        entity.getBatteryCapacityKwh(),
                        entity.getBatteryVoltageV(),
                        entity.getBatteryMaxChargeSpeedKw()
                ),
                Performance.of(
                        entity.getHorsePowerHp(),
                        entity.getAccelerationSec(),
                        entity.getTopSpeedKmh()
                ),
                Amount.ofCents(entity.getMonthlyLeasePriceCents(), Currency.valueOf(entity.getMonthlyLeasePriceCurrency())),
                Percentage.of(first.getStateOfHealth()),
                Range.ofKm(first.getMeasuredRangeKm())
        );

        for (int i = 1; i < records.size(); i++) {
            vehicle.updateBatteryHealthMeasurement(
                    Percentage.of(records.get(i).getStateOfHealth()),
                    Range.ofKm(records.get(i).getMeasuredRangeKm())
            );
        }

        VehicleStatus persistedStatus = VehicleStatus.valueOf(entity.getStatus());
        if (persistedStatus == VehicleStatus.REMOVED) {
            vehicle.remove();
        }

        return vehicle;
    }
}
