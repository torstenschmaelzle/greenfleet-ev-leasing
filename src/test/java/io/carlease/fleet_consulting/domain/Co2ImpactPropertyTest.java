package io.carlease.fleet_consulting.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.LeaseTerm;
import io.carlease.shared.domain.Currency;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Co2ImpactPropertyTest {

    //Daily km: 10 (urban delivery) to 500 (inter-city logistics); below 10 is not meaningful
    //for fleet consultations, above 700 is unrealistic for electric fleets due to range and charging time constraints.
    private static final int MIN_DAILY_KM = 10;
    private static final int MAX_DAILY_KM = 700;

    //Fleet size: 1 (pilot) to 50 (large SME fleet); our target customer segment is SMEs.
    private static final int MIN_VEHICLES = 1;
    private static final int MAX_VEHICLES = 50;

    //kWh/100km: 10.0 (most efficient city EVs, e.g. Smart EQ) to 35.0 (large SUVs in winter).
    //At 35 kWh/100km the EV emits 35 * 12 / 100 = 4.2 g/km — still far below 150 g/km ICE. Also, vehicles above 35 kWh/100km are not in our catalogue.
    private static final double MIN_KWH = 10.0;
    private static final double MAX_KWH = 35.0;

    private FleetRequirements req(int dailyKm, int vehicles) {
        return FleetRequirements.of(Kilometre.of(dailyKm), NumberOfVehicles.of(vehicles), ChargingSetup.of(ChargingType.COMPANY_CHARGER, ElectricityRate.of(Amount.ofCents(30, Currency.CHF))), Optional.empty(), LeaseTerm.of(36), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Property 1: An EV fleet always saves CO2 compared to ICE within our catalogue's consumption range.
     *
     * Why this property matters: the core promise of the fleet consulting service is that switching
     * to EVs reduces CO2 emissions. At the maximum realistic consumption of 35 kWh/100km with our
     * Swiss grid factor of 12 g CO2/kWh, the EV produces at most 4.2 g/km, which is far below the
     * 150 g/km ICE baseline. If annualSavingsKg ever turned non-positive, the recommendation
     * engine would be promoting vehicles that are worse for the environment than ICE cars.
     */
    @Property
    void evAlwaysSavesMoreCo2ThanIce(
            @ForAll @IntRange(min = MIN_DAILY_KM, max = MAX_DAILY_KM) int dailyKm,
            @ForAll @IntRange(min = MIN_VEHICLES, max = MAX_VEHICLES) int vehicles,
            @ForAll @DoubleRange(min = MIN_KWH, max = MAX_KWH) double kwhPer100km
    ) {
        Co2Impact impact = Co2Impact.calculate(req(dailyKm, vehicles), kwhPer100km);

        assertThat(impact.getAnnualSavingsKg()).as("EV fleet must always reduce CO2 compared to ICE baseline within catalogue kWh range").isGreaterThan(BigDecimal.ZERO);
    }

    /**
     * Property 2: A more energy-efficient EV always yields strictly more CO2 savings (monotonicity).
     *
     * Why this property matters: VehicleScorer ranks vehicles by CO2 savings. If this ordering
     * were violated, a less efficient vehicle appeared to save more CO2 for example, the eco dimension
     * of the scoring would mislead consultants into recommending environmentally worse options.
     * This property is the formal guarantee that "efficiency" and "eco score" point in the same direction.
     */
    @Property
    void higherEfficiencyMeansMoreCo2Savings(
            @ForAll @IntRange(min = MIN_DAILY_KM, max = MAX_DAILY_KM) int dailyKm,
            @ForAll @IntRange(min = MIN_VEHICLES, max = MAX_VEHICLES) int vehicles,
            @ForAll @DoubleRange(min = MIN_KWH, max = MAX_KWH) double kwhEfficient,
            @ForAll @DoubleRange(min = MIN_KWH, max = MAX_KWH) double kwhInefficient
    ) {
        //require a meaningful difference so BigDecimal rounding (4 dp) cannot erase the gap
        Assume.that(kwhInefficient - kwhEfficient >= 0.1);

        FleetRequirements req = req(dailyKm, vehicles);
        Co2Impact savingsEfficient   = Co2Impact.calculate(req, kwhEfficient);
        Co2Impact savingsInefficient = Co2Impact.calculate(req, kwhInefficient);

        assertThat(savingsEfficient.getAnnualSavingsKg()).as("Lower kWh/100km must always produce higher annual CO2 savings").isGreaterThan(savingsInefficient.getAnnualSavingsKg());
    }

    /**
     * Property 3: Higher daily usage produces proportionally more CO2 savings (monotonicity in km).
     *
     * Why this property matters: fleet consultants present annualised CO2 impact to justify
     * electrification investments. If a fleet that drives twice as far did not save more CO2,
     * the calculation would be wrong and the business case would be built on incorrect numbers.
     * A bug here (e.g. accidentally using a fixed km value) would silently produce identical
     * savings for any route length, undermining the per-customer customisation of recommendations.
     */
    @Property
    void moreDailyKmMeansMoreCo2Savings(
            @ForAll @IntRange(min = MIN_DAILY_KM, max = MAX_DAILY_KM / 2) int kmLow,
            @ForAll @IntRange(min = MIN_DAILY_KM, max = MAX_DAILY_KM) int kmHigh,
            @ForAll @IntRange(min = MIN_VEHICLES, max = MAX_VEHICLES) int vehicles,
            @ForAll @DoubleRange(min = MIN_KWH, max = MAX_KWH) double kwhPer100km
    ) {
        Assume.that(kmHigh > kmLow);

        Co2Impact savingsLow  = Co2Impact.calculate(req(kmLow,  vehicles), kwhPer100km);
        Co2Impact savingsHigh = Co2Impact.calculate(req(kmHigh, vehicles), kwhPer100km);

        assertThat(savingsHigh.getAnnualSavingsKg()).as("A fleet driving more km/day must show higher annual CO2 savings").isGreaterThan(savingsLow.getAnnualSavingsKg());
    }
}
