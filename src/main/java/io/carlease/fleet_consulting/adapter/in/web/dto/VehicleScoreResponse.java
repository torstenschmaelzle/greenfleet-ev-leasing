package io.carlease.fleet_consulting.adapter.in.web.dto;

public class VehicleScoreResponse {

    private final double rangeScore;
    private final double co2Score;
    private final double costScore;
    private final double finalScore;

    public VehicleScoreResponse(double rangeScore, double co2Score, double costScore, double finalScore) {
        this.rangeScore = rangeScore;
        this.co2Score = co2Score;
        this.costScore = costScore;
        this.finalScore = finalScore;
    }

    public double getRangeScore() { return rangeScore; }
    public double getCo2Score() { return co2Score; }
    public double getCostScore() { return costScore; }
    public double getFinalScore() { return finalScore; }
}
