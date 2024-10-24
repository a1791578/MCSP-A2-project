package com.example.foodxplore;

import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.util.List;

public class Restaurant {
    private String name;
    private String address;
    private double rating;
    private List<PhotoMetadata> photoMetadatas;
    private List<String> types;

    public Restaurant(String name, String address, double rating, List<PhotoMetadata> photoMetadatas, List<String> types) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.photoMetadatas = photoMetadatas;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public List<PhotoMetadata> getPhotoMetadatas() {
        return photoMetadatas;
    }

    public List<String> getTypes() {
        return types;
    }
}


