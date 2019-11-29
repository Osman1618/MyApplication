package com.example.myapplication;

public class User {

    public String fullname, programcode, programname, username;
    public int points;

    public User(String fullname, String programcode, String programname, String username, int points) {
        this.fullname = fullname;
        this.programcode = programcode;
        this.programname = programname;
        this.username = username;
        this.points = points;
    }

    public User(){

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProgramcode() {
        return programcode;
    }

    public void setProgramcode(String programcode) {
        this.programcode = programcode;
    }

    public String getProgramname() {
        return programname;
    }

    public void setProgramname(String programname) {
        this.programname = programname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
