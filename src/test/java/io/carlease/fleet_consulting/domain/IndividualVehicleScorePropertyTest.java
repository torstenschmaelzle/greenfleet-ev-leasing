package io.carlease.fleet_consulting.domain;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualVehicleScorePropertyTest {

    //Component scores are clamped to [0, 1] by VehicleScorer before reaching this method.
    //The property tests verify that IndividualVehicleScore independently preserves these bounds so the invariant holds even if called outside the normal VehicleScorer pipeline.
    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 1.0;

    //Minimum meaningful difference between two range scores for the monotonicity property.
    //With the smallest possible weight (1/100 = 0.01), a delta of 0.01 produces a finalScore difference of at least 0.0001
    private static final double MIN_RANGE_DELTA = 0.01;

    /**
     * Generates any valid ScoringWeights: three non-negative integers summing to exactly 100.
     * The approach: pick rangeWeight freely in [0,100], then co2Weight in [0, 100-rangeWeight],
     * and derive costWeight = 100 - rangeWeight - co2Weight (always ≥ 0).
     */
    @Provide
    Arbitrary<ScoringWeights> validWeights() {
        return Arbitraries.integers().between(0, 100).flatMap(rangeW -> Arbitraries.integers().between(0, 100 - rangeW).map(co2W -> {
                    int costW = 100 - rangeW - co2W;
                    return ScoringWeights.of(rangeW, co2W, costW);
                })
        );
    }

    /**
     * Same generator but guarantees rangeWeight ≥ 1, required for the monotonicity property.
     */
    @Provide
    Arbitrary<ScoringWeights> validWeightsWithPositiveRange() {
        return Arbitraries.integers().between(1, 100).flatMap(rangeW -> Arbitraries.integers().between(0, 100 - rangeW).map(co2W -> {
                    int costW = 100 - rangeW - co2W;
                    return ScoringWeights.of(rangeW, co2W, costW);
                })
        );
    }

    /**
     * Property 1: The final score always lies within [0.0, 1.0] when component scores are in [0, 1]
     * and weights are valid (non-negative and summing to 100).
     *
     * Why this property matters: scores are shown to consultants as a ranking signal and
     * implicitly interpreted as percentages. A score above 1.0 would misrepresent a vehicle
     * as "more than 100% good", while a negative score is nonsensical. The invariant must hold
     * for every possible weight configuration.
     *
     * Constraint choices:
     * Component scores in [0.0, 1.0]: matches the clamping applied by VehicleScorer.
     * Any valid weight triple
     */
    @Property
    void finalScoreIsWithinUnitInterval(
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double rangeScore,
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double co2Score,
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double costScore,
            @ForAll("validWeights") ScoringWeights weights
    ) {
        IndividualVehicleScore score = IndividualVehicleScore.of(rangeScore, co2Score, costScore, weights);

        assertThat(score.getFinalScore()).as("Final score must lie within [0.0, 1.0] for any valid inputs and weights").isBetween(MIN_SCORE, MAX_SCORE);
    }

    /**
     * Property 2: The final score is strictly monotone in the range score when rangeWeight > 0.
     *
     * Why this property matters: the whole purpose of IndividualVehicleScore is to produce
     * a ranking that reflects how well each dimension contributes. If a vehicle's range score
     * increases but its final score does not follow, the ranking is broken, meaning that a consultant
     * requesting range-heavy weighting would see incorrect results.
     *
     * Constraint choices:
     *   rangeLow and rangeHigh both in [0, 1], with a gap of at least MIN_RANGE_DELTA (0.01).
     *   The gap is required because with the smallest weight (1 out of 100) the induced
     *   finalScore difference is (rangeHigh - rangeLow) * 0.01; a gap < 0.001 could be
     *   swallowed by IEEE 754 double rounding.
     *   rangeWeight ≥ 1: the property is vacuously false for rangeWeight = 0, so we exclude it.
     */
    @Property
    void finalScoreIsMonotoneInRangeScore(
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double rangeLow,
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double rangeHigh,
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double co2Score,
            @ForAll @DoubleRange(min = MIN_SCORE, max = MAX_SCORE) double costScore,
            @ForAll("validWeightsWithPositiveRange") ScoringWeights weights
    ) {
        Assume.that(rangeHigh - rangeLow >= MIN_RANGE_DELTA);

        double scoreLow  = IndividualVehicleScore.of(rangeLow,  co2Score, costScore, weights).getFinalScore();
        double scoreHigh = IndividualVehicleScore.of(rangeHigh, co2Score, costScore, weights).getFinalScore();

        assertThat(scoreHigh).as("A higher range score must produce a strictly higher final score when rangeWeight > 0").isGreaterThan(scoreLow);
    }
}
