package com.manuel.aulainventario.models;

public class Kinder {
    private String id, name, address, gardenKey, referenceImageUrl;

    public Kinder() {
    }

    public Kinder(String id, String name, String address, String gardenKey, String referenceImageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.gardenKey = gardenKey;
        this.referenceImageUrl = referenceImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGardenKey() {
        return gardenKey;
    }

    public void setGardenKey(String gardenKey) {
        this.gardenKey = gardenKey;
    }

    public String getReferenceImageUrl() {
        return referenceImageUrl;
    }

    public void setReferenceImageUrl(String referenceImageUrl) {
        this.referenceImageUrl = referenceImageUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}