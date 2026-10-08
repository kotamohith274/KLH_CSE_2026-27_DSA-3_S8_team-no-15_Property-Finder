package com.example.propertyfinder.model;

import java.util.List;

public class Property {
    private final int id;
    private final String title;
    private final String location;
    private final String type;
    private final double price;
    private final String description;
    private List<String> detectedAmenities;

    public Property(int id, String title, String location, String type, double price, String description) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public List<String> getDetectedAmenities() { return detectedAmenities; }
    public void setDetectedAmenities(List<String> amenities) { this.detectedAmenities = amenities; }
}