package com.manuel.aulainventario.models;

public class Active {
    private long number, amount, price, total, timestamp;
    private String id, idTeacher, key, description, condition;

    public Active() {
    }

    public Active(long number, long amount, long price, long total, long timestamp, String id, String idTeacher, String key, String description, String condition) {
        this.number = number;
        this.amount = amount;
        this.price = price;
        this.total = total;
        this.timestamp = timestamp;
        this.id = id;
        this.idTeacher = idTeacher;
        this.key = key;
        this.description = description;
        this.condition = condition;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}