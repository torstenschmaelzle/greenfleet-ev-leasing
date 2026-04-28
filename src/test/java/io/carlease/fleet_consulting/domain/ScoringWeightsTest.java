package io.carlease.fleet_consulting.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoringWeightsTest {

    @Nested
    class Validation {

        @Test
        void negativeRangeWeightIsRejected() {
            assertThatThrownBy(() -> ScoringWeights.of(-1, 51, 50))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void negativeCo2WeightIsRejected() {
            assertThatThrownBy(() -> ScoringWeights.of(51, -1, 50))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void negativeCostWeightIsRejected() {
            assertThatThrownBy(() -> ScoringWeights.of(51, 50, -1))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }

        @Test
        void weightsThatDoNotSumToHundredAreRejected() {
            assertThatThrownBy(() -> ScoringWeights.of(30, 30, 30))
                    .isInstanceOf(InvalidFleetAttributeException.class);
        }
    }

    @Test
    void storesWeightsCorrectly() {
        // of(rangeWeight, co2Weight, costWeight)
        ScoringWeights weights = ScoringWeights.of(50, 30, 20);

        assertThat(weights.getRangeWeight()).isEqualTo(50);
        assertThat(weights.getCo2Weight()).isEqualTo(30);
        assertThat(weights.getCostWeight()).isEqualTo(20);
    }

    @Test
    void zeroWeightIsValid() {
        ScoringWeights weights = ScoringWeights.of(0, 100, 0);

        assertThat(weights.getCo2Weight()).isEqualTo(100);
    }

    @Nested
    class FactoryMethods {

        @Test
        void balancedSumsToHundred() {
            ScoringWeights w = ScoringWeights.balanced();
            assertThat(w.getRangeWeight() + w.getCo2Weight() + w.getCostWeight()).isEqualTo(100);
        }

        @Test
        void ecoHasHighCo2Weight() {
            ScoringWeights w = ScoringWeights.eco();
            assertThat(w.getCo2Weight()).isEqualTo(60);
            assertThat(w.getRangeWeight() + w.getCo2Weight() + w.getCostWeight()).isEqualTo(100);
        }

        @Test
        void economicHasHighCostWeight() {
            ScoringWeights w = ScoringWeights.economic();
            assertThat(w.getCostWeight()).isEqualTo(60);
            assertThat(w.getRangeWeight() + w.getCo2Weight() + w.getCostWeight()).isEqualTo(100);
        }
    }
}
