package com.example.solar.model;

import java.util.Map;

public class CompareResponse {
    private double nasaYearly;
    private double copernicusYearly;
    private Map<String, Double> nasaMonthly;
    private Map<String, Double> copernicusMonthly;

    public CompareResponse(double nasaYearly, double copernicusYearly,
                           Map<String, Double> nasaMonthly, Map<String, Double> copernicusMonthly) {
        this.nasaYearly = nasaYearly;
        this.copernicusYearly = copernicusYearly;
        this.nasaMonthly = nasaMonthly;
        this.copernicusMonthly = copernicusMonthly;
    }

    public double getNasaYearly() {
        return nasaYearly;
    }

    public double getCopernicusYearly() {
        return copernicusYearly;
    }

    public Map<String, Double> getNasaMonthly() {
        return nasaMonthly;
    }

    public Map<String, Double> getCopernicusMonthly() {
        return copernicusMonthly;
    }
}
