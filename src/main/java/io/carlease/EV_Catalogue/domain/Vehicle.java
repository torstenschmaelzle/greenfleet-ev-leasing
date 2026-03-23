package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.*;

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
        if (vehicleId == null) throw new InvalidVehicleAttributeException("vehicleId", "must not be null");
        if (vehicleSpecification == null) throw new InvalidVehicleAttributeException("vehicleSpecification", "must not be null");
        if (batterySpecification == null) throw new InvalidVehicleAttributeException("batterySpecification", "must not be null");
        if (performance == null) throw new InvalidVehicleAttributeException("performance", "must not be null");
        if (monthlyLeasePrice == null) throw new InvalidVehicleAttributeException("monthlyLeasePrice", "must not be null");
        if (initialHealthRecords == null) throw new InvalidVehicleAttributeException("initialHealthRecords", "must not be null");
        if (initialHealthRecords.isEmpty()) {
            throw new InvalidVehicleAttributeException("initialHealthRecords", "must not be empty");
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
        if (specification == null) throw new InvalidVehicleAttributeException("specification", "must not be null");
        this.vehicleSpecification = specification;
    }

    public void updateMonthlyLeasePrice(Amount price) {
        if (price == null) throw new InvalidVehicleAttributeException("price", "must not be null");
        this.monthlyLeasePrice = price;
    }

    public void updateBatterySpecification(BatterySpecification specification) {
        if (specification == null) throw new InvalidVehicleAttributeException("specification", "must not be null");
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
            throw new VehicleNotAvailableException(vehicleId, status);
        }
        this.lock = new ReservationLock(duration);
        this.status = VehicleStatus.RESERVED;
    }

    public void releaseLock() {
        if (status != VehicleStatus.RESERVED) {
            throw new VehicleLockViolationException(vehicleId, status);
        }
        this.lock = null;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void confirmContract() {
        if (status != VehicleStatus.RESERVED) {
            throw new VehicleContractViolationException(vehicleId, status);
        }
        this.lock = null;
        this.status = VehicleStatus.UNDER_CONTRACT;
    }

    public void remove() {
        if (status == VehicleStatus.REMOVED) {
            throw new VehicleRemovalException(vehicleId, status);
        }
        if (status != VehicleStatus.AVAILABLE) {
            throw new VehicleRemovalException(vehicleId, status);
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
