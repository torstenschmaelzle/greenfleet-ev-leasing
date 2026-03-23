package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.*;

import static java.util.Objects.requireNonNull;

@Entity
public class Vehicle {

    @Identity
    private final VehicleId vehicleId;
    private VehicleSpecification vehicleSpecification;
    private Amount monthlyLeasePrice;
    private VehicleStatus status;
    private BatterySpecification batterySpecification;
    private final List<BatteryHealthRecord> healthRecords;
    private Performance performance;
    private ReservationLock lock;

    public Vehicle(VehicleId vehicleId,
                   VehicleSpecification vehicleSpecification,
                   BatterySpecification batterySpecification,
                   Performance performance,
                   Amount monthlyLeasePrice,
                   List<BatteryHealthRecord> initialHealthRecords) {
        requireNonNull(vehicleId);
        requireNonNull(vehicleSpecification);
        requireNonNull(batterySpecification);
        requireNonNull(performance);
        requireNonNull(monthlyLeasePrice);
        requireNonNull(initialHealthRecords);
        if (initialHealthRecords.isEmpty()) {
            throw new IllegalArgumentException("initialHealthRecords must not be empty");
        }
        this.vehicleId = vehicleId;
        this.vehicleSpecification = vehicleSpecification;
        this.batterySpecification = batterySpecification;
        this.performance = performance;
        this.monthlyLeasePrice = monthlyLeasePrice;
        this.status = VehicleStatus.AVAILABLE;
        this.lock = null;
        this.healthRecords = new ArrayList<>(initialHealthRecords);
    }

    public void updateVehicleSpecification(VehicleSpecification specification) {
        requireNonNull(specification);
        this.vehicleSpecification = specification;
    }

    public void updateMonthlyLeasePrice(Amount price) {
        requireNonNull(price);
        this.monthlyLeasePrice = price;
    }

    public void updateBatterySpecification(BatterySpecification specification) {
        requireNonNull(specification);
        this.batterySpecification = specification;
    }

    public void addBatteryHealthRecord(Percentage stateOfHealth, Range measuredRangeKm) {
        BatteryHealthRecord record = new BatteryHealthRecord(stateOfHealth, measuredRangeKm);
        this.healthRecords.add(record);
    }

    public BatteryHealthRecord getLatestBatteryHealthRecord() {
        return healthRecords.stream()
                .max(Comparator.comparing(BatteryHealthRecord::getMeasuredAt))
                .orElseThrow();
    }

    public Amount getMonthlyLeasePrice() {
        return monthlyLeasePrice;
    }

    public void lockVehicle(LockDuration duration) {
        if (status != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Vehicle can only be locked when AVAILABLE, current status: " + status);
        }
        this.lock = new ReservationLock(duration);
        this.status = VehicleStatus.RESERVED;
    }

    public void releaseLock() {
        if (status != VehicleStatus.RESERVED) {
            throw new IllegalStateException(
                    "Lock can only be released when RESERVED, current status: " + status);
        }
        this.lock = null;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void confirmContract() {
        if (status != VehicleStatus.RESERVED) {
            throw new IllegalStateException(
                    "Contract can only be confirmed when RESERVED, current status: " + status);
        }
        this.lock = null;
        this.status = VehicleStatus.UNDER_CONTRACT;
    }

    public void remove() {
        if (status == VehicleStatus.REMOVED) {
            throw new IllegalStateException("Vehicle is already REMOVED");
        }
        if (status != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Vehicle can only be removed when AVAILABLE, current status: " + status);
        }
        this.status = VehicleStatus.REMOVED;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public VehicleId getId() {
        return vehicleId;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public VehicleSpecification getVehicleSpecification() {
        return vehicleSpecification;
    }

    public BatterySpecification getBatterySpecification() {
        return batterySpecification;
    }

    public Performance getPerformance() {
        return performance;
    }

    public List<BatteryHealthRecord> getHealthRecords() {
        return Collections.unmodifiableList(healthRecords);
    }

    public Optional<ReservationLock> getLock() {
        return Optional.ofNullable(lock);
    }
}
