package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AggregateRoot
public class Vehicle {

    @Identity
    private final VehicleId vehicleId;
    private VehicleSpecification vehicleSpecification;
    private Amount monthlyLeasePrice;
    private VehicleStatus status;
    private BatterySpecification batterySpecification;
    private final List<BatteryHealthMeasurement> healthMeasurements;
    private Performance performance;
    private ReservationLock lock;

    public static Vehicle create(VehicleId vehicleId,
                                VehicleSpecification vehicleSpecification,
                                BatterySpecification batterySpecification,
                                Performance performance,
                                Amount monthlyLeasePrice,
                                Percentage initialStateofHealth,
                                 Range initialMessureRangeKM) {
        BatteryHealthMeasurement initialHealthMeasurement = new BatteryHealthMeasurement(initialStateofHealth, initialMessureRangeKM);
        return new Vehicle(vehicleId, vehicleSpecification, batterySpecification, performance, monthlyLeasePrice, List.of(initialHealthMeasurement));
    }

    private Vehicle(VehicleId vehicleId,
                   VehicleSpecification vehicleSpecification,
                   BatterySpecification batterySpecification,
                   Performance performance,
                   Amount monthlyLeasePrice,
                   List<BatteryHealthMeasurement> initialHealthMeasurements) {
        if (vehicleId == null) throw new InvalidVehicleAttributeException("vehicleId", "must not be null");
        if (vehicleSpecification == null) throw new InvalidVehicleAttributeException("vehicleSpecification", "must not be null");
        if (batterySpecification == null) throw new InvalidVehicleAttributeException("batterySpecification", "must not be null");
        if (performance == null) throw new InvalidVehicleAttributeException("performance", "must not be null");
        if (monthlyLeasePrice == null) throw new InvalidVehicleAttributeException("monthlyLeasePrice", "must not be null");
        if (initialHealthMeasurements == null) throw new InvalidVehicleAttributeException("initialHealthMeasurements", "must not be null");
        if (initialHealthMeasurements.isEmpty()) {
            throw new InvalidVehicleAttributeException("initialHealthMeasurements", "must not be empty");
        }
        this.vehicleId = vehicleId;
        this.vehicleSpecification = vehicleSpecification;
        this.batterySpecification = batterySpecification;
        this.performance = performance;
        this.monthlyLeasePrice = monthlyLeasePrice;
        this.status = VehicleStatus.AVAILABLE;
        this.lock = null;
        this.healthMeasurements = new ArrayList<>(initialHealthMeasurements);
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

    public void updateBatteryHealthMeasurement(Percentage stateOfHealth, Range measuredRangeKm) {
        BatteryHealthMeasurement measurement =  new BatteryHealthMeasurement(stateOfHealth, measuredRangeKm);
        this.addBatteryHealthMeasurement(measurement);
    }

    private void addBatteryHealthMeasurement(BatteryHealthMeasurement measurement) {
        if (measurement == null) throw new InvalidVehicleAttributeException("measurement", "must not be null");
        this.healthMeasurements.add(measurement);
    }


    private BatteryHealthMeasurement getLatestBatteryHealthMeasurement() {
        if (healthMeasurements.isEmpty()) {
            throw new InvalidVehicleAttributeException("healthMeasurements", "no measurements present");
        }
        return healthMeasurements.get(healthMeasurements.size() - 1);
    }

    public Percentage getLatestStateOfHealth() {
        return getLatestBatteryHealthMeasurement().getStateOfHealth();
    }

    public Range getLatestMeasuredRangeKm() {
        return getLatestBatteryHealthMeasurement().getMeasuredRangeKm();
    }

    public Amount getMonthlyLeasePrice() {
        return monthlyLeasePrice;
    }

    public void lockVehicle(LockDuration duration) {
        clearExpiredLockIfPresent();
        if (status != VehicleStatus.AVAILABLE) {
            throw new VehicleNotAvailableException(vehicleId, status);
        }
        this.lock = new ReservationLock(duration);
        this.status = VehicleStatus.RESERVED;
    }

    private void clearExpiredLockIfPresent() {
        if (status == VehicleStatus.RESERVED && lock != null && lock.isExpired()) {
            this.lock = null;
            this.status = VehicleStatus.AVAILABLE;
        }
    }


    public void releaseLock() {
        if (status != VehicleStatus.RESERVED) {
            throw new VehicleLockViolationException(vehicleId, status);
        }
        this.lock = null;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void assignToContract() {
        if (status != VehicleStatus.RESERVED) {
            throw new VehicleContractViolationException(vehicleId, status);
        }
        this.lock = null;
        this.status = VehicleStatus.UNDER_CONTRACT;
    }

    public void remove() {
        if (status != VehicleStatus.AVAILABLE) {
            throw new VehicleRemovalException(vehicleId, status);
        }
        this.status = VehicleStatus.REMOVED;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public VehicleId getVehicleId() {
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

    public List<BatteryHealthMeasurement> getHealthMeasurements() {
        return Collections.unmodifiableList(healthMeasurements);
    }

    public Optional<ReservationLock> getLock() {
        return Optional.ofNullable(lock);
    }

}
