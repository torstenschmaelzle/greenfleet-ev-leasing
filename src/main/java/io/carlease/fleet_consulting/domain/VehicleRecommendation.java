package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.Entity;

@Entity
public class VehicleRecommendation {

    private final Amount monthlyLeasePrice;
    private final double kwhPer100km;
    private final Kilometre rangeKm;
    private final RangeFit rangeFit;
    private final TotalCostOfOwnership tco;
    private final Co2Impact co2Impact;
    private final SuitabilityScore suitabilityScore;
    private final String modelName;
    private final VehicleType vehicleType;

    private VehicleRecommendation(Amount monthlyLeasePrice, double kwhPer100km, Kilometre rangeKm,
                                   RangeFit rangeFit, TotalCostOfOwnership tco,
                                   Co2Impact co2Impact, SuitabilityScore suitabilityScore, String modelName, VehicleType vehicleType) {
        this.monthlyLeasePrice = monthlyLeasePrice;
        this.kwhPer100km = kwhPer100km;
        this.rangeKm = rangeKm;
        this.rangeFit = rangeFit;
        this.tco = tco;
        this.co2Impact = co2Impact;
        this.suitabilityScore = suitabilityScore;
        this.modelName = modelName;
        this.vehicleType = vehicleType;
    }

    public static VehicleRecommendation evaluate(Amount monthlyLeasePrice, double kwhPer100km,
                                                  Kilometre rangeKm, FleetRequirements req, ScoringStrategy strategy,String modelName,
                                                 VehicleType vehicleType) {
        if (monthlyLeasePrice == null)
            throw new InvalidFleetAttributeException("monthlyLeasePrice", "must not be null");
        if (rangeKm == null) throw new InvalidFleetAttributeException("rangeKm", "must not be null");
        if (req == null) throw new InvalidFleetAttributeException("req", "must not be null");
        if (strategy == null) throw new InvalidFleetAttributeException("strategy", "must not be null");
        if (modelName == null || modelName.isBlank()) throw new InvalidFleetAttributeException("modelName", "must not be blank");
        if (vehicleType == null) throw new InvalidFleetAttributeException("vehicleType", "must not be null");

        RangeFit rangeFit = RangeFit.evaluate(req.getDailyRouteKm(), rangeKm);
        TotalCostOfOwnership tco = TotalCostOfOwnership.calculate(req, monthlyLeasePrice, kwhPer100km, rangeKm);
        Co2Impact co2Impact = Co2Impact.calculate(req, kwhPer100km);
        SuitabilityScore suitabilityScore = strategy.calculate(rangeFit, tco, co2Impact, req);

        return new VehicleRecommendation(monthlyLeasePrice, kwhPer100km, rangeKm,
                rangeFit, tco, co2Impact, suitabilityScore, modelName, vehicleType);
    }

    public Amount getMonthlyLeasePrice() {
        return monthlyLeasePrice;
    }

    public double getKwhPer100km() {
        return kwhPer100km;
    }

    public Kilometre getRangeKm() {
        return rangeKm;
    }

    public RangeFit getRangeFit() {
        return rangeFit;
    }

    public TotalCostOfOwnership getTco() {
        return tco;
    }

    public Co2Impact getCo2Impact() {
        return co2Impact;
    }

    public SuitabilityScore getSuitabilityScore() {
        return suitabilityScore;
    }

    public String getModelName() {
        return modelName;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}
