package com.manuel.aulainventario.models;

public class Teacher {
    private String id, email, teachername, phone, kinder;
    private long timestamp;

    public Teacher() {
    }

    public Teacher(String id, String email, String teachername, String phone, String kinder, long timestamp) {
        this.id = id;
        this.email = email;
        this.teachername = teachername;
        this.phone = phone;
        this.kinder = kinder;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKinder() {
        return kinder;
    }

    public void setKinder(String kinder) {
        this.kinder = kinder;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
