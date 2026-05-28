package io.carlease.ev_catalogue.domain;

import io.carlease.shared.domain.Amount;
import org.jmolecules.ddd.annotation.Factory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Factory
public class VehicleFactory {

    public static Vehicle createVehicle(
            VehicleId vehicleId,
            VehicleSpecification vehicleSpecification,
            BatterySpecification batterySpecification,
            Performance performance,
            Amount monthlyLeasePrice,
            Percentage initialStateOfHealth,
            Range initialMeasuredRangeKm) {
        requireNonNull(vehicleId);
        requireNonNull(vehicleSpecification);
        requireNonNull(batterySpecification);
        requireNonNull(performance);
        requireNonNull(monthlyLeasePrice);
        requireNonNull(initialStateOfHealth);
        requireNonNull(initialMeasuredRangeKm);
        BatteryHealthMeasurement initial = new BatteryHealthMeasurement(initialStateOfHealth, initialMeasuredRangeKm);
        return new Vehicle(vehicleId, vehicleSpecification, batterySpecification, performance, monthlyLeasePrice, List.of(initial));
    }

    public static Vehicle restoreVehicle(
            VehicleId vehicleId,
            VehicleSpecification vehicleSpecification,
            BatterySpecification batterySpecification,
            Performance performance,
            Amount monthlyLeasePrice,
            List<Percentage> statesOfHealth,
            List<Range> measuredRanges) {
        requireNonNull(vehicleId);
        requireNonNull(vehicleSpecification);
        requireNonNull(batterySpecification);
        requireNonNull(performance);
        requireNonNull(monthlyLeasePrice);
        requireNonNull(statesOfHealth);
        requireNonNull(measuredRanges);
        List<BatteryHealthMeasurement> measurements = new ArrayList<>();
        for (int i = 0; i < statesOfHealth.size(); i++) {
            measurements.add(new BatteryHealthMeasurement(statesOfHealth.get(i), measuredRanges.get(i)));
        }
        return new Vehicle(vehicleId, vehicleSpecification, batterySpecification, performance, monthlyLeasePrice, measurements);
    }
}
