package com.example.solar.model;

public class EstimationRequest {

    private double latitude;
    private double longitude;
    private double panelSize;
    private double efficiency;
    private String source; // "nasa" or "copernicus"

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPanelSize() {
        return panelSize;
    }

    public void setPanelSize(double panelSize) {
        this.panelSize = panelSize;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
