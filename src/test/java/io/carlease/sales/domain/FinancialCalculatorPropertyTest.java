package io.carlease.sales.domain;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialCalculatorPropertyTest {

    //12–48 months covers the realistic 1–4 year EV lease window shorter than 12 is unusual, longer than 48 is rare in practice
    private static final double MIN_PERIODS = 12.0;
    private static final double MAX_PERIODS = 48.0;

    //0.1–1.5% monthly = 1.2–18% annually; below 0.1% is essentially free money, above 1.5%/month (18% annual) is not seen in regular car lease contracts
    private static final double MIN_INTEREST_PERCENT = 0.1;
    private static final double MAX_INTEREST_PERCENT = 1.5;

    //30000–150000 CHF matches the actual cars in our catalogue: cheapest is the Renault Zoe (~30k), most expensive are the Porsche Taycan and Tesla Model S (~130–150k).
    private static final double MIN_PV = -150000.0;
    private static final double MAX_PV = -30000.0;

    //Residual value is non-negative; upper annotation bound is 30% of the cheapest car in our current car range (0.3 * 30000 = 9000)
    private static final double MIN_FV = 0.0;
    private static final double MAX_FV = 9000.0;

    //Residual must stay below 30% of the vehicle's value, since typical EV lease residuals sit between 20–35%; keeping it at 30% ensures the monthly payment stays meaningful
    private static final double MAX_RESIDUAL_RATIO = 0.3;

    //Advance payment fixed at 0, since usually, it's a standard end-of-period structure;
    private static final double ADVANCE_PAYMENT = 0.0;

    @Property
    void monthlyPaymentIsAtLeast25Chf(
            @ForAll @DoubleRange(min = MIN_PERIODS, max = MAX_PERIODS) double n,
            @ForAll @DoubleRange(min = MIN_INTEREST_PERCENT, max = MAX_INTEREST_PERCENT) double iInPercent,
            @ForAll @DoubleRange(min = MIN_PV, max = MAX_PV) double pv,
            @ForAll @DoubleRange(min = MIN_FV, max = MAX_FV) double fv
    ) {
        //Discard combinations where the residual exceeds 30% of this specific car's value
        Assume.that(fv <= Math.abs(pv) * MAX_RESIDUAL_RATIO);

        double payment = FinancialCalculator.pmt(n, iInPercent, pv, fv, ADVANCE_PAYMENT);
        assertThat(payment).as("Monthly lease payment should never be less than CHF 25").isGreaterThanOrEqualTo(25.0);
    }
}
