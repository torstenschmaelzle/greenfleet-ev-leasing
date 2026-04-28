package io.carlease.fleet_consulting.domain;

public class DomainConstants {

    public static final int CO2_FACTOR_PER_KWH = 12;
    public static final int ICE_GRAMS_PER_KM = 150;
    public static final double RANGE_SAFETY_FACTOR = 1.2;

    public static final double DEFAULT_INTEREST_RATE_PER_YEAR = 2.9;

    public static final int BALANCED_RANGE_W = 33;
    public static final int BALANCED_CO2_W = 33;
    public static final int BALANCED_COST_W = 34;

    public static final int ECO_RANGE_W = 20;
    public static final int ECO_CO2_W = 60;
    public static final int ECO_COST_W = 20;

    public static final int ECONOMIC_RANGE_W = 20;
    public static final int ECONOMIC_CO2_W = 20;
    public static final int ECONOMIC_COST_W = 60;

    private DomainConstants() {
    }
}
