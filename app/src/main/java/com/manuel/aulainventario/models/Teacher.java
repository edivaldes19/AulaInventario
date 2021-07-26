package com.manuel.aulainventario.models;

public class Teacher {
    private String id, idKinder, email, teachername, phone, turn, grade, group;
    private long timestamp;

    public Teacher() {
    }

    public Teacher(String id, String idKinder, String email, String teachername, String phone, String turn, String grade, String group, long timestamp) {
        this.id = id;
        this.idKinder = idKinder;
        this.email = email;
        this.teachername = teachername;
        this.phone = phone;
        this.turn = turn;
        this.grade = grade;
        this.group = group;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdKinder() {
        return idKinder;
    }

    public void setIdKinder(String idKinder) {
        this.idKinder = idKinder;
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

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "turn='" + turn + '\'' +
                ", grade='" + grade + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}