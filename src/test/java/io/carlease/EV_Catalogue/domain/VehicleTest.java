package io.carlease.EV_Catalogue.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VehicleTest {

    //helper functions and test data builders

    private static final String VALID_VIN = "WBA12345678901234";

    private VehicleId vehicleId() {
        return VehicleId.of(VALID_VIN);
    }

    private VehicleSpecification specification() {
        return VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")));
    }

    private BatterySpecification batterySpec() {
        return BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11"));
    }

    private Performance performance() {
        return Performance.of(286, new BigDecimal("6.8"), 180);
    }

    private Amount monthlyPrice() {
        return Amount.of(75000L, Currency.CHF);
    }

    private Vehicle freshVehicle() {
        var healthRecord = new BatteryHealthRecord(Percentage.of(new BigDecimal("95")), Range.ofKm(440));
        return new Vehicle(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), List.of(healthRecord));
    }

    //tests
    @Nested
    class vehicleLifecycle {

        @Test
        void vehicleIsCreatedWithStatusAvailable() {
            var vehicle = freshVehicle();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
            assertThat(vehicle.getHealthRecords()).hasSize(1);
        }

        @Test
        void vehicleCreationRejectedWithoutHealthRecord() {
            assertThatThrownBy(() -> new Vehicle(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), List.of())).isInstanceOf(InvalidVehicleAttributeException.class);
        }
    }

    @Nested
    class lockingAndReservation {

        @Test
        void availableVehicleIsLocked() {
            var vehicle = freshVehicle();
            var duration = LockDuration.ofHours(48);

            vehicle.lockVehicle(duration);

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
            var lock = vehicle.getLock();
            assertThat(lock).isPresent();
            assertThat(lock.get().getLockId()).isNotNull();
            assertThat(lock.get().getExpiresAt()).isEqualTo(lock.get().getLockedAt().plus(duration.getHours(), java.time.temporal.ChronoUnit.HOURS));
        }

        @Test
        void lockRejectedOnNonAvailableVehicle() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(24));

            assertThatThrownBy(() -> vehicle.lockVehicle(LockDuration.ofHours(24))).isInstanceOf(VehicleNotAvailableException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }

        @Test
        void lockRejectedOnRemovedVehicle() {
            var vehicle = freshVehicle();
            vehicle.remove();

            assertThatThrownBy(() -> vehicle.lockVehicle(LockDuration.ofHours(24))).isInstanceOf(VehicleNotAvailableException.class);
        }

        @Test
        void lockIsReleased() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(24));

            vehicle.releaseLock();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void releaseRejectedWhenNotReserved() {
            var vehicle = freshVehicle();

            assertThatThrownBy(vehicle::releaseLock).isInstanceOf(VehicleLockViolationException.class);
        }
    }

    @Nested
    class contractConfirmation {

        @Test
        void contractConfirmedOnReservedVehicle() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(24));

            vehicle.confirmContract();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void contractConfirmationRejectedWhenNotReserved() {
            var vehicle = freshVehicle();

            assertThatThrownBy(vehicle::confirmContract).isInstanceOf(VehicleContractViolationException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        }

        @Test
        void contractConfirmationRejectedWhenAlreadyUnderContract() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(24));
            vehicle.confirmContract();

            assertThatThrownBy(vehicle::confirmContract).isInstanceOf(VehicleContractViolationException.class);
        }
    }

    @Nested
    class removal {

        @Test
        void availableVehicleIsRemoved() {
            var vehicle = freshVehicle();
            vehicle.remove();
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.REMOVED);
        }

        @Test
        void removalRejectedOnReservedVehicle() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(24));

            assertThatThrownBy(vehicle::remove).isInstanceOf(VehicleRemovalException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }

        @Test
        void removedIsTerminalState() {
            var vehicle = freshVehicle();
            vehicle.remove();

            assertThatThrownBy(vehicle::remove).isInstanceOf(VehicleRemovalException.class);
        }
    }

    @Nested
    class batteryHealthRecords {

        @Test
        void healthRecordIsAdded() {
            var vehicle = freshVehicle();
            int sizeBefore = vehicle.getHealthRecords().size();
            var newSoh = Percentage.of(new BigDecimal("87"));
            var newRange = Range.ofKm(400);

            vehicle.addBatteryHealthRecord(newSoh, newRange);

            assertThat(vehicle.getHealthRecords()).hasSize(sizeBefore + 1);
            var latest = vehicle.getLatestBatteryHealthRecord();
            assertThat(latest.getRecordId()).isNotNull();
            assertThat(latest.getMeasuredAt()).isNotNull();
            assertThat(latest.getStateOfHealth()).isEqualTo(newSoh);
            assertThat(latest.getMeasuredRangeKm()).isEqualTo(newRange);
        }

        @Test
        void latestHealthRecordIsTheMostRecent() throws InterruptedException {
            var vehicle = freshVehicle();
            vehicle.addBatteryHealthRecord(Percentage.of(new BigDecimal("90")), Range.ofKm(430));
            //make sure the next record has a later timestamp
            Thread.sleep(1);
            var newerSoh = Percentage.of(new BigDecimal("85"));

            vehicle.addBatteryHealthRecord(newerSoh, Range.ofKm(395));

            assertThat(vehicle.getLatestBatteryHealthRecord().getStateOfHealth()).isEqualTo(newerSoh);
        }
    }

    @Nested
    class specificationAndPriceUpdates {

        @Test
        void leasePriceIsUpdated() {
            var vehicle = freshVehicle();
            var newPrice = Amount.of(80000L, Currency.CHF);

            vehicle.updateMonthlyLeasePrice(newPrice);

            assertThat(vehicle.getMonthlyLeasePrice()).isEqualTo(newPrice);
        }

        @Test
        void vehicleSpecificationIsReplaced() {
            var vehicle = freshVehicle();
            var newSpec = VehicleSpecification.of("Tesla", "Model 3", Range.ofKm(500), VehicleType.SEDAN, EnergyConsumption.of(new BigDecimal("14")));

            vehicle.updateVehicleSpecification(newSpec);

            assertThat(vehicle.getVehicleSpecification()).isEqualTo(newSpec);
        }
    }
    @Nested
    class concreteBddScenarios {

        @Test
        void addingNewBmwIx3Vehicle() {
            var vin = VehicleId.of("BMWIX300000000001");
            var spec = VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")));
            var battery = BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("11"));
            var perf = Performance.of(286, new BigDecimal("6.8"), 180);
            var price = Amount.of(75000L, Currency.CHF);
            var healthRecord = new BatteryHealthRecord(Percentage.of(new BigDecimal("100")), Range.ofKm(460));

            var vehicle = new Vehicle(vin, spec, battery, perf, price, List.of(healthRecord));

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getVehicleSpecification().getBrand()).isEqualTo("BMW");
            assertThat(vehicle.getVehicleSpecification().getModel()).isEqualTo("iX3");
            assertThat(vehicle.getVehicleSpecification().getRangeKm()).isEqualTo(Range.ofKm(460));
            assertThat(vehicle.getVehicleSpecification().getVehicleType()).isEqualTo(VehicleType.SUV);
            assertThat(vehicle.getMonthlyLeasePrice()).isEqualTo(Amount.of(75000L, Currency.CHF));
        }

        @Test
        void lockingVehicleDuringNegotiation() {
            var vehicle = freshVehicle();
            var duration = LockDuration.ofHours(2);

            vehicle.lockVehicle(duration);

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
            var lock = vehicle.getLock();
            assertThat(lock).isPresent();
            assertThat(lock.get().getExpiresAt()).isEqualTo(lock.get().getLockedAt().plus(duration.getHours(), java.time.temporal.ChronoUnit.HOURS));
        }

        @Test
        void reservationLockExpiresWithoutContract() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));

            vehicle.releaseLock();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void vehicleIsRemovedFromSystem() {
            var vehicle = freshVehicle();
            vehicle.remove();
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.REMOVED);
        }
    }

    @Nested
    class domainInvariants {

        @Test
        void vehicleIdVinMustNotBeNull() {
            assertThatThrownBy(() -> VehicleId.of(null)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleIdVinMustNotBeBlank() {
            assertThatThrownBy(() -> VehicleId.of("   ")).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void rangeValueKmMustBeGreaterThanZero() {
            assertThatThrownBy(() -> Range.ofKm(0)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void energyConsumptionKwhPer100kmMustBeGreaterThanZero() {
            assertThatThrownBy(() -> EnergyConsumption.of(BigDecimal.ZERO)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void percentageValueMustNotBeNegative() {
            assertThatThrownBy(() -> Percentage.of(new BigDecimal("-1"))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void percentageValueMustNotExceed100() {
            assertThatThrownBy(() -> Percentage.of(new BigDecimal("101"))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void lockDurationHoursMustBeGreaterThanZero() {
            assertThatThrownBy(() -> LockDuration.ofHours(0)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountCurrencyMustNotBeNull() {
            assertThatThrownBy(() -> Amount.of(100L, null)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountAddRequiresSameCurrency() {
            var chf = Amount.of(100L, Currency.CHF);
            var eur = Amount.of(100L, Currency.EUR);

            assertThatThrownBy(() -> chf.add(eur)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountSubtractRequiresSameCurrency() {
            var chf = Amount.of(100L, Currency.CHF);
            var eur = Amount.of(50L, Currency.EUR);

            assertThatThrownBy(() -> chf.subtract(eur)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleSpecificationBrandMustNotBeNull() {
            assertThatThrownBy(() -> VehicleSpecification.of(null, "iX3", Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleSpecificationModelMustNotBeNull() {
            assertThatThrownBy(() -> VehicleSpecification.of("BMW", null, Range.ofKm(460), VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleSpecificationRangeKmMustNotBeNull() {
            assertThatThrownBy(() -> VehicleSpecification.of("BMW", "iX3", null, VehicleType.SUV, EnergyConsumption.of(new BigDecimal("17")))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleSpecificationVehicleTypeMustNotBeNull() {
            assertThatThrownBy(() -> VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), null, EnergyConsumption.of(new BigDecimal("17")))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleSpecificationConsumptionMustNotBeNull() {
            assertThatThrownBy(() -> VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV,null)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void batterySpecificationCapacityKwhMustBePositive() {
            assertThatThrownBy(() -> BatterySpecification.of(BigDecimal.ZERO, new BigDecimal("400"), new BigDecimal("11"))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void batterySpecificationVoltageMustBePositive() {
            assertThatThrownBy(() -> BatterySpecification.of(new BigDecimal("80"), BigDecimal.ZERO, new BigDecimal("11"))).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void batterySpecificationMaxChargeSpeedMustBePositive() {
            assertThatThrownBy(() -> BatterySpecification.of(new BigDecimal("80"), new BigDecimal("400"), BigDecimal.ZERO)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleHealthRecordsMustNeverBeEmpty() {
            assertThatThrownBy(() -> new Vehicle(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), List.of())).isInstanceOf(InvalidVehicleAttributeException.class);
        }
    }
}
