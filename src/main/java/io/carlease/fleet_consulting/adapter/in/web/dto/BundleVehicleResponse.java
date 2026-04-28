package io.carlease.fleet_consulting.adapter.in.web.dto;

public class BundleVehicleResponse {

    private final String vin;
    private final String brand;
    private final String model;
    private final String vehicleType;
    private final int rangeKm;
    private final long monthlyInstallmentCents;
    private final String currency;
    private final double co2SavingsKgPerYear;
    private final VehicleScoreResponse score;

    public BundleVehicleResponse(String vin, String brand, String model, String vehicleType,
                                  int rangeKm, long monthlyInstallmentCents, String currency,
                                  double co2SavingsKgPerYear, VehicleScoreResponse score) {
        this.vin = vin;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.rangeKm = rangeKm;
        this.monthlyInstallmentCents = monthlyInstallmentCents;
        this.currency = currency;
        this.co2SavingsKgPerYear = co2SavingsKgPerYear;
        this.score = score;
    }

    public String getVin() { return vin; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getVehicleType() { return vehicleType; }
    public int getRangeKm() { return rangeKm; }
    public long getMonthlyInstallmentCents() { return monthlyInstallmentCents; }
    public String getCurrency() { return currency; }
    public double getCo2SavingsKgPerYear() { return co2SavingsKgPerYear; }
    public VehicleScoreResponse getScore() { return score; }
}
