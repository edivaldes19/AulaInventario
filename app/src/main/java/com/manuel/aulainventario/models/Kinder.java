package com.manuel.aulainventario.models;

public class Kinder {
    private String name, direction;

    public Kinder() {
    }

    public Kinder(String name, String direction) {
        this.name = name;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return name;
    }
}