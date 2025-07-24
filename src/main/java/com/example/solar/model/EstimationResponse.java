package com.example.solar.model;

import java.util.Map;

public class EstimationResponse {
    private double kWhYearly;
    private Map<String, Double> monthlyBreakdown;

    public EstimationResponse(double kWhYearly, Map<String, Double> monthlyBreakdown) {
        this.kWhYearly = kWhYearly;
        this.monthlyBreakdown = monthlyBreakdown;
    }

    public double getkWhYearly() {
        return kWhYearly;
    }

    public void setkWhYearly(double kWhYearly) {
        this.kWhYearly = kWhYearly;
    }

    public Map<String, Double> getMonthlyBreakdown() {
        return monthlyBreakdown;
    }

    public void setMonthlyBreakdown(Map<String, Double> monthlyBreakdown) {
        this.monthlyBreakdown = monthlyBreakdown;
    }
}
