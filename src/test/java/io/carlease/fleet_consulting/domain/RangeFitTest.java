package io.carlease.fleet_consulting.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RangeFitTest {

    @Nested
    class Validation {

        @Test
        void dailyKmMustNotBeNull() {
            assertThatThrownBy(() -> RangeFit.evaluate(null, Kilometre.of(200)))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void vehicleRangeMustNotBeNull() {
            assertThatThrownBy(() -> RangeFit.evaluate(Kilometre.of(100), null))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }

    @Nested
    class Evaluation {

        @Test
        void rangeFitIsSufficientWhenVehicleRangeExceedsRequired() {
            // dailyKm=100 → required = ceil(100 * 1.2) = 120, vehicleRange=200 >= 120
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(200));

            assertThat(rangeFit.isSufficient()).isTrue();
            assertThat(rangeFit.getRequiredKm()).isEqualTo(Kilometre.of(120));
            assertThat(rangeFit.getActualKm()).isEqualTo(Kilometre.of(200));
        }

        @Test
        void rangeFitIsInsufficientWhenVehicleRangeBelowRequired() {
            // dailyKm=100 → required = 120, vehicleRange=100 < 120
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(100));

            assertThat(rangeFit.isSufficient()).isFalse();
        }

        @Test
        void rangeFitIsSufficientWhenVehicleRangeExactlyEqualsRequired() {
            // dailyKm=100 → required = 120, vehicleRange=120 == 120
            RangeFit rangeFit = RangeFit.evaluate(Kilometre.of(100), Kilometre.of(120));

            assertThat(rangeFit.isSufficient()).isTrue();
        }
    }
}
