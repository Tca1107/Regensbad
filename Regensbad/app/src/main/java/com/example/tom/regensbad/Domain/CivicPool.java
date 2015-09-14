package com.example.tom.regensbad.Domain;


import java.net.URI;

/**
 * Created by Sebastian on 26.08.2015.
 */
public class CivicPool implements Comparable<CivicPool> {

    private String name;
    private String type;
    private double lati;
    private double longi;
    private String phoneNumber;
    private String website;
    private String openTime;
    private String closeTime;
    private String picPath;
    private int civicID;
    private double currentDistance;
    private float currentRating;
    private String openTimeSat;
    private String closeTimeSat;
    private String openTimeSun;
    private String closeTimeSun;


    public CivicPool(String name, String type, double lati, double longi, String phoneNumber, String website, String openTime, String closeTime,
                     String picPath, int civicID, double currentDistance, float currentRating, String openTimeSat, String closeTimeSat,
                     String openTimeSun, String closeTimeSun) {
        this.name = name;
        this.type = type;
        this.lati = lati;
        this.longi = longi;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.picPath = picPath;
        this.civicID = civicID;
        this.currentDistance = currentDistance;
        this.currentRating = currentRating;
        this.openTimeSat = openTimeSat;
        this.closeTimeSat = closeTimeSat;
        this.openTimeSun = openTimeSun;
        this.closeTimeSun = closeTimeSun;

    }

    @Override
    public int compareTo (CivicPool civicPool) {
        Double currentDistanceAsDouble = new Double(currentDistance);
        return currentDistanceAsDouble.compareTo(civicPool.getCurrentDistance());
    }

    public void setCurrentRating (float rating) {
        this.currentRating = rating;
    }

    public float getCurrentRating() {
        return currentRating;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public String getPicPath() {
        return picPath;
    }

    public int getID() {
        return civicID;
    }

    public double getCurrentDistance() {
        return currentDistance;
    }

    public void setDecimalPlacesInCurrentDistance(double distance) {
        currentDistance = distance;
    }

    public String getOpenTimeSat () {return openTimeSat;}

    public String getCloseTimeSat () {return closeTimeSat;}

    public String getOpenTimeSun () {return openTimeSun;}

    public String getCloseTimeSun () {return closeTimeSun;}

}