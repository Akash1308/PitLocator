package com.example.pitlocator.modals;


public class ComplainModal {
    String phoneNumber,image,condition;
    double lat,lon;
    int range;
    public ComplainModal(){ }

    public ComplainModal(String phoneNumber, String image, String condition, int range) {
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.condition = condition;
        this.range = range; }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getComplainimg() {
        return image;
    }

    public void setComplainimg(String complainimg) {
        this.image = complainimg;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public int getRange() {
        return range;
    }
    public void setRange(int range) {
        this.range = range;
    }
}
