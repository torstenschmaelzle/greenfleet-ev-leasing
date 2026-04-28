package io.carlease.fleet_consulting.domain;

public interface ScoringStrategy {
    SuitabilityScore calculate(RangeFit rangeFit,
                               TotalCostOfOwnership tco,
                               Co2Impact co2Impact,
                               FleetRequirements requirements);
}