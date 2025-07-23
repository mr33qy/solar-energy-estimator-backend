package com.example.solar.controller;

import com.example.solar.model.CompareResponse;
import com.example.solar.model.EstimationRequest;
import com.example.solar.model.EstimationResponse;
import com.example.solar.service.CopernicusDataService;
import com.example.solar.service.SolarIrradianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class EstimationController {

    @Autowired
    private SolarIrradianceService irradianceService;

    @Autowired
    private CopernicusDataService copernicusDataService;

    // ----------- Ping Test ----------
    @GetMapping("/ping")
    public String ping() {
        return "Backend is working!";
    }

    // ----------- Solar Lookup ----------
    @GetMapping("/solar")
    public Map<String, Object> getSolarData(@RequestParam double lat, @RequestParam double lon) {
        System.out.println("🌎 [Solar API] Fetching for: " + lat + ", " + lon);

        double avgIrradiance = 4.5; // fallback

        try {
            avgIrradiance = irradianceService.getNasaIrradiance(lat, lon);
        } catch (Exception e) {
            System.err.println("⚠️ Could not fetch NASA data. Using fallback 4.5.");
        }

        double annualRadiation = avgIrradiance * 365;
        double potentialOutput = annualRadiation; // 1kW panel assumption
        double co2Savings = potentialOutput * 0.7; // estimate 0.7kg CO₂ per kWh

        String[] months = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };
        double[] distribution = { 0.07, 0.08, 0.09, 0.10, 0.11, 0.10, 0.09, 0.09, 0.08, 0.07, 0.06, 0.06 };

        Map<String, Double> monthly = new LinkedHashMap<>();
        for (int i = 0; i < months.length; i++) {
            double monthlyKWh = Math.round(annualRadiation * distribution[i] * 100.0) / 100.0;
            monthly.put(months[i], monthlyKWh);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("annualRadiation", Math.round(annualRadiation * 100.0) / 100.0);
        response.put("potentialOutput", Math.round(potentialOutput * 100.0) / 100.0);
        response.put("co2Savings", Math.round(co2Savings * 100.0) / 100.0);
        response.put("monthlyRadiation", monthly);

        return response;
    }

    // ----------- Estimation ----------
    @PostMapping("/estimate")
    public EstimationResponse estimateEnergy(@RequestBody EstimationRequest request) {
        System.out.println("🔆 [Estimate] New request for location: " + request.getLocation());

        String source = (request.getSource() == null || request.getSource().trim().isEmpty())
                ? "nasa"
                : request.getSource();

        double avgIrradiance;
        if ("copernicus".equalsIgnoreCase(source)) {
            avgIrradiance = copernicusDataService.getAverageIrradiance();
            System.out.println("🌍 Using Copernicus data");
        } else {
            avgIrradiance = 4.5; // fallback or NASA
            System.out.println("☀️ Using NASA/default fallback");
        }

        double yearly = request.getPanelSize() * avgIrradiance * 365 * request.getEfficiency();

        Map<String, Double> monthly = new LinkedHashMap<>();
        double[] distribution = { 0.07, 0.08, 0.09, 0.10, 0.11, 0.10, 0.09, 0.09, 0.08, 0.07, 0.06, 0.06 };
        String[] months = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };

        for (int i = 0; i < 12; i++) {
            double monthlyKWh = Math.round(yearly * distribution[i] * 100.0) / 100.0;
            monthly.put(months[i], monthlyKWh);
        }

        return new EstimationResponse(Math.round(yearly * 100.0) / 100.0, monthly);
    }

    // ----------- Compare Copernicus vs NASA ----------
    @PostMapping("/compare")
    public CompareResponse compareSources(@RequestBody EstimationRequest request) {
        System.out.println("🔍 [Compare] Comparing sources for panel size: " + request.getPanelSize());

        double panel = request.getPanelSize();
        double eff = request.getEfficiency();
        int days = 365;

        double nasaIrradiance = 4.5; // fallback
        double copernicusIrradiance = copernicusDataService.getAverageIrradiance();

        double nasaYearly = panel * nasaIrradiance * days * eff;
        double copYearly = panel * copernicusIrradiance * days * eff;

        String[] months = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };
        double[] distribution = { 0.07, 0.08, 0.09, 0.10, 0.11, 0.10, 0.09, 0.09, 0.08, 0.07, 0.06, 0.06 };

        Map<String, Double> nasaMonthly = new LinkedHashMap<>();
        Map<String, Double> copMonthly = new LinkedHashMap<>();

        for (int i = 0; i < 12; i++) {
            nasaMonthly.put(months[i], Math.round(nasaYearly * distribution[i] * 100.0) / 100.0);
            copMonthly.put(months[i], Math.round(copYearly * distribution[i] * 100.0) / 100.0);
        }

        return new CompareResponse(
                Math.round(nasaYearly * 100.0) / 100.0,
                Math.round(copYearly * 100.0) / 100.0,
                nasaMonthly,
                copMonthly
        );
    }

    // ----------- Copernicus Test ----------
    @GetMapping("/copernicus/test")
    public Map<String, Double> testCopernicus() {
        System.out.println("🛰️ Testing Copernicus data fetch...");
        return copernicusDataService.getMonthlyIrradiance();
    }
}
