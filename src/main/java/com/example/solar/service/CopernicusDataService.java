package com.example.solar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public class CopernicusDataService {

    private final Map<String, Double> monthlyData;

    public CopernicusDataService() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> data = null;

        try (InputStream input = getClass().getResourceAsStream("/data/copernicus_solar_data.json")) {
            if (input == null) {
                throw new RuntimeException("❌ File not found: /data/copernicus_solar_data.json");
            }

            Map<?, ?> json = mapper.readValue(input, Map.class);
            data = (Map<String, Double>) json.get("monthly");
            System.out.println("✅ Copernicus monthly data loaded.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load Copernicus data: " + e.getMessage());
            e.printStackTrace();
        }

        this.monthlyData = data;
    }

    public Map<String, Double> getMonthlyIrradiance() {
        return monthlyData;
    }

    public double getAverageIrradiance() {
        return monthlyData != null
                ? monthlyData.values().stream().mapToDouble(v -> v).average().orElse(4.5)
                : 4.5; // fallback if loading failed
    }
}
