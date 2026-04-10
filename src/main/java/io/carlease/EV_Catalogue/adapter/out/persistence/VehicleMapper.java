package io.carlease.EV_Catalogue.adapter.out.persistence;

import io.carlease.EV_Catalogue.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    public static VehicleJpaEntity mapToJpaEntity(Vehicle vehicle) {
        List<BatteryHealthRecordEmbeddable> healthRecords = vehicle.getHealthRecords().stream()
                .map(record -> new BatteryHealthRecordEmbeddable(
                        record.getRecordId().toString(),
                        record.getMeasuredAt(),
                        record.getStateOfHealth().getValue(),
                        record.getMeasuredRangeKm().getValue()
                ))
                .collect(Collectors.toList());

        ReservationLock lock = vehicle.getLock().orElse(null);

        return new VehicleJpaEntity(
                vehicle.getId().getId(),
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

    // TODO: Vehicle has no restore-constructor — status is always reconstructed as AVAILABLE.
    // A VehicleFactory.restore(...) in the domain package is needed for correct status reconstitution.
    public static Vehicle mapToDomain(VehicleJpaEntity entity) {
        List<BatteryHealthRecord> healthRecords = entity.getHealthRecords().stream()
                .map(r -> new BatteryHealthRecord(
                        Percentage.of(r.getStateOfHealth()),
                        Range.ofKm(r.getMeasuredRangeKm())
                ))
                .collect(Collectors.toList());

        return new Vehicle(
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
                Amount.of(entity.getMonthlyLeasePriceCents(), Currency.valueOf(entity.getMonthlyLeasePriceCurrency())),
                healthRecords
        );
    }
}
