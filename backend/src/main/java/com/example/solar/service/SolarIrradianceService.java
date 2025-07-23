package com.example.solar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@Service
public class SolarIrradianceService {

    private Map<String, Map<String, Double>> copernicusData = Collections.emptyMap();
    private static final double DEFAULT_IRRADIANCE = 4.5;

    public SolarIrradianceService() {
        loadCopernicusData();
    }

    private void loadCopernicusData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            copernicusData = mapper.readValue(
                    new File("copernicus_solar_data.json"),
                    new TypeReference<Map<String, Map<String, Double>>>() {}
            );
            System.out.println("✅ Copernicus data loaded successfully.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load Copernicus data: " + e.getMessage());
        }
    }

    public double getNasaIrradiance(double latitude, double longitude) {
        String url = String.format(
                "https://power.larc.nasa.gov/api/temporal/daily/point?parameters=ALLSKY_SFC_SW_DWN" +
                        "&community=RE&latitude=%f&longitude=%f&format=JSON&start=20240101&end=20241231",
                latitude, longitude
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            Map<String, Object> properties = (Map<String, Object>) response.get("properties");
            Map<String, Object> parameter = (Map<String, Object>) properties.get("parameter");
            Map<String, Double> dailyData = (Map<String, Double>) parameter.get("ALLSKY_SFC_SW_DWN");

            return dailyData.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(DEFAULT_IRRADIANCE);

        } catch (Exception e) {
            System.err.println("⚠️ Error fetching NASA data: " + e.getMessage());
            return DEFAULT_IRRADIANCE;
        }
    }

    public double getCopernicusIrradiance(double latitude, double longitude) {
        if (copernicusData.isEmpty()) return DEFAULT_IRRADIANCE;

        String key = String.format("%.2f,%.2f", latitude, longitude);
        Map<String, Double> values = copernicusData.get(key);

        if (values == null) {
            System.err.println("⚠️ No Copernicus data found for: " + key);
            return DEFAULT_IRRADIANCE;
        }

        return values.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(DEFAULT_IRRADIANCE);
    }
}
