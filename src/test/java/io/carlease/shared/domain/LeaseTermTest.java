package io.carlease.shared.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeaseTermTest {

    // --- Happy path: all allowed values ---

    @Test
    void givenAllowedMonths_whenOf_thenCreatesLeaseTerm() {
        assertThat(LeaseTerm.of(12).getMonths()).isEqualTo(12);
        assertThat(LeaseTerm.of(24).getMonths()).isEqualTo(24);
        assertThat(LeaseTerm.of(36).getMonths()).isEqualTo(36);
        assertThat(LeaseTerm.of(48).getMonths()).isEqualTo(48);
        assertThat(LeaseTerm.of(60).getMonths()).isEqualTo(60);
    }

    @Test
    void givenFourYears_whenOfYears_thenReturns48Months() {
        assertThat(LeaseTerm.ofYears(4).getMonths()).isEqualTo(48);
    }

    // --- Rejection: values not in the whitelist ---

    @Test
    void givenDisallowedMonths_whenOf_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> LeaseTerm.of(13))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenZeroMonths_whenOf_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> LeaseTerm.of(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNegativeMonths_whenOf_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> LeaseTerm.of(-12))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- Equality ---

    @Test
    void givenTwoLeaseTermsWithSameMonths_whenEquals_thenTrue() {
        assertThat(LeaseTerm.of(36)).isEqualTo(LeaseTerm.of(36));
    }

    @Test
    void givenTwoLeaseTermsWithDifferentMonths_whenEquals_thenFalse() {
        assertThat(LeaseTerm.of(36)).isNotEqualTo(LeaseTerm.of(48));
    }
}
