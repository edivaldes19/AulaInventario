package com.manuel.aulainventario.models;

public class Consumption {
    private long number, amount, timestamp;
    private String id, idTeacher, description;

    public Consumption() {
    }

    public Consumption(long number, long amount, long timestamp, String id, String idTeacher, String description) {
        this.number = number;
        this.amount = amount;
        this.timestamp = timestamp;
        this.id = id;
        this.idTeacher = idTeacher;
        this.description = description;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(String idTeacher) {
        this.idTeacher = idTeacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}