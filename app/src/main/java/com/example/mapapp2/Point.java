package com.example.mapapp2;

public class Point {
    private double latitude;
    private double longitude;
    private String name;

    public Point(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Point(double latitude, double longitude) {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
}
