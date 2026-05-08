package io.carlease.fleet_consulting.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialCalculatorTest {

    @Test
    void pmt() {
        double pmt = FinancialCalculator.pmt(48, 3.7 / 12, -40_000, 0, 0);
        assertThat(pmt).isEqualTo(897.8022814470006);
    }

}
