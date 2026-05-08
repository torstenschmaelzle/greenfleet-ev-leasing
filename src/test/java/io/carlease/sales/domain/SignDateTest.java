package io.carlease.sales.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignDateTest {

    @Test
    void twoSignDatesWithSameValue_areEqual() {
        assertThat(SignDate.of(2018, 8, 4)).isEqualTo(SignDate.of(2018, 8, 4));
    }

    @Test
    void year_returnsCorrectValue() {
        assertThat(SignDate.of(2026, 3, 15).year()).isEqualTo(2026);
    }

    @Test
    void month_returnsCorrectValue() {
        assertThat(SignDate.of(2026, 3, 15).month()).isEqualTo(3);
    }

    @Test
    void day_returnsCorrectValue() {
        assertThat(SignDate.of(2026, 3, 15).day()).isEqualTo(15);
    }
}
