package io.carlease.EV_Catalogue.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        return Amount.ofCents(75000L, Currency.CHF);
    }

    private Vehicle freshVehicle() {
        var healthRecord = new BatteryHealthMeasurement(Percentage.of(new BigDecimal("95")), Range.ofKm(440));
        return Vehicle.create(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), List.of(healthRecord));
    }

    //tests
    @Nested
    class vehicleLifecycle {

        @Test
        void vehicleIsCreatedWithStatusAvailable() {
            var vehicle = freshVehicle();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
            assertThat(vehicle.getHealthMeasurements()).hasSize(1);
        }

    }

    @Nested
    class lockingAndReservation {

        @Test
        void availableVehicleIsLocked() {
            var vehicle = freshVehicle();
            var duration = LockDuration.ofHours(2);

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
            vehicle.lockVehicle(LockDuration.ofHours(2));

            assertThatThrownBy(() -> vehicle.lockVehicle(LockDuration.ofHours(2))).isInstanceOf(VehicleNotAvailableException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }

        @Test
        void lockRejectedOnRemovedVehicle() {
            var vehicle = freshVehicle();
            vehicle.remove();

            assertThatThrownBy(() -> vehicle.lockVehicle(LockDuration.ofHours(2))).isInstanceOf(VehicleNotAvailableException.class);
        }

        @Test
        void lockIsReleased() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));

            vehicle.releaseLock();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void releaseRejectedWhenNotReserved() {
            var vehicle = freshVehicle();

            assertThatThrownBy(vehicle::releaseLock).isInstanceOf(VehicleLockViolationException.class);
        }

        @Test
        void expiredLockIsAutoClearedAndVehicleCanBeLockedAgain() throws Exception {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));
            var lock = vehicle.getLock().get();
            Field lockedAt = ReservationLock.class.getDeclaredField("lockedAt");
            lockedAt.setAccessible(true);
            lockedAt.set(lock, Instant.now().minus(3, ChronoUnit.HOURS));

            vehicle.lockVehicle(LockDuration.ofHours(2));

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
            assertThat(vehicle.getLock()).isPresent();
        }

        @Test
        void lockVehicleRejectedWhenUnderContract() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));
            vehicle.assignToContract();

            assertThatThrownBy(() -> vehicle.lockVehicle(LockDuration.ofHours(2)))
                    .isInstanceOf(VehicleNotAvailableException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }
    }

    @Nested
    class contractConfirmation {

        @Test
        void contractConfirmedOnReservedVehicle() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));

            vehicle.assignToContract();

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
            assertThat(vehicle.getLock()).isEmpty();
        }

        @Test
        void contractConfirmationRejectedWhenNotReserved() {
            var vehicle = freshVehicle();

            assertThatThrownBy(vehicle::assignToContract).isInstanceOf(VehicleContractViolationException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        }

        @Test
        void contractConfirmationRejectedWhenAlreadyUnderContract() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));
            vehicle.assignToContract();

            assertThatThrownBy(vehicle::assignToContract).isInstanceOf(VehicleContractViolationException.class);
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
            vehicle.lockVehicle(LockDuration.ofHours(2));

            assertThatThrownBy(vehicle::remove).isInstanceOf(VehicleRemovalException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        }

        @Test
        void removedIsTerminalState() {
            var vehicle = freshVehicle();
            vehicle.remove();

            assertThatThrownBy(vehicle::remove).isInstanceOf(VehicleRemovalException.class);
        }

        @Test
        void removeRejectedWhenUnderContract() {
            var vehicle = freshVehicle();
            vehicle.lockVehicle(LockDuration.ofHours(2));
            vehicle.assignToContract();

            assertThatThrownBy(vehicle::remove).isInstanceOf(VehicleRemovalException.class);
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.UNDER_CONTRACT);
        }
    }

    @Nested
    class batteryHealthRecords {

        @Test
        void healthRecordIsAdded() {
            var vehicle = freshVehicle();
            int sizeBefore = vehicle.getHealthMeasurements().size();
            var newSoh = Percentage.of(new BigDecimal("87"));
            var newRange = Range.ofKm(400);

            vehicle.addBatteryHealthMeasurement(new BatteryHealthMeasurement(newSoh, newRange));

            assertThat(vehicle.getHealthMeasurements()).hasSize(sizeBefore + 1);
            var latest = vehicle.getLatestBatteryHealthMeasurement();
            assertThat(latest.getMeasurementId()).isNotNull();
            assertThat(latest.getMeasuredAt()).isNotNull();
            assertThat(latest.getStateOfHealth()).isEqualTo(newSoh);
            assertThat(latest.getMeasuredRangeKm()).isEqualTo(newRange);
        }

        @Test
        void latestHealthRecordIsTheMostRecent() throws InterruptedException {
            var vehicle = freshVehicle();
            vehicle.addBatteryHealthMeasurement(new BatteryHealthMeasurement(Percentage.of(new BigDecimal("90")), Range.ofKm(430)));
            //make sure the next record has a later timestamp
            Thread.sleep(1);
            var newerSoh = Percentage.of(new BigDecimal("85"));

            vehicle.addBatteryHealthMeasurement(new BatteryHealthMeasurement(newerSoh, Range.ofKm(395)));

            assertThat(vehicle.getLatestBatteryHealthMeasurement().getStateOfHealth()).isEqualTo(newerSoh);
        }
    }

    @Nested
    class specificationAndPriceUpdates {

        @Test
        void leasePriceIsUpdated() {
            var vehicle = freshVehicle();
            var newPrice = Amount.ofCents(80000L, Currency.CHF);

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
            var price = Amount.ofCents(75000L, Currency.CHF);
            var healthRecord = new BatteryHealthMeasurement(Percentage.of(new BigDecimal("100")), Range.ofKm(460));

            var vehicle = Vehicle.create(vin, spec, battery, perf, price, List.of(healthRecord));

            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
            assertThat(vehicle.getVehicleSpecification().getBrand()).isEqualTo("BMW");
            assertThat(vehicle.getVehicleSpecification().getModel()).isEqualTo("iX3");
            assertThat(vehicle.getVehicleSpecification().getRangeKm()).isEqualTo(Range.ofKm(460));
            assertThat(vehicle.getVehicleSpecification().getVehicleType()).isEqualTo(VehicleType.SUV);
            assertThat(vehicle.getMonthlyLeasePrice()).isEqualTo(Amount.ofCents(75000L, Currency.CHF));
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
        void vehicleIdVinTooShortThrowsException() {
            assertThatThrownBy(() -> VehicleId.of("WBA123456789012")).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleIdVinTooLongThrowsException() {
            assertThatThrownBy(() -> VehicleId.of("WBA123456789012345X")).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void vehicleIdVinNonAlphanumericThrowsException() {
            assertThatThrownBy(() -> VehicleId.of("WBA-2345678901234")).isInstanceOf(InvalidVehicleAttributeException.class);
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
            assertThatThrownBy(() -> Amount.ofCents(100L, null)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountInCentsMustNotBeNegative() {
            assertThatThrownBy(() -> Amount.ofCents(-1L, Currency.CHF)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountConvertToNullCurrencyThrowsException() {
            var amount = Amount.ofCents(10000L, Currency.CHF);
            assertThatThrownBy(() -> amount.convertTo(null, 1.1)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountConvertToAppliesRateAndChangesCurrency() {
            var chf = Amount.ofCents(10000L, Currency.CHF);

            var eur = chf.convertTo(Currency.EUR, 0.95);

            assertThat(eur.getCurrency()).isEqualTo(Currency.EUR);
            assertThat(eur.getAmountInCents()).isEqualTo(Math.round(10000L * 0.95));
        }

        @Test
        void amountAddRequiresSameCurrency() {
            var chf = Amount.ofCents(100L, Currency.CHF);
            var eur = Amount.ofCents(100L, Currency.EUR);

            assertThatThrownBy(() -> chf.add(eur)).isInstanceOf(InvalidVehicleAttributeException.class);
        }

        @Test
        void amountSubtractRequiresSameCurrency() {
            var chf = Amount.ofCents(100L, Currency.CHF);
            var eur = Amount.ofCents(50L, Currency.EUR);

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
            assertThatThrownBy(() -> VehicleSpecification.of("BMW", "iX3", Range.ofKm(460), VehicleType.SUV, null)).isInstanceOf(InvalidVehicleAttributeException.class);
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
            assertThatThrownBy(() -> Vehicle.create(vehicleId(), specification(), batterySpec(), performance(), monthlyPrice(), List.of())).isInstanceOf(InvalidVehicleAttributeException.class);
        }
    }
}