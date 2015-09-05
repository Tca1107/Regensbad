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


    public CivicPool(String name, String type, double lati, double longi, String phoneNumber, String website, String openTime, String closeTime, String picPath, int civicID, double currentDistance) {
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

    }

    @Override
    public int compareTo (CivicPool civicPool) {
        Double currentDistanceAsDouble = new Double(currentDistance);
        return currentDistanceAsDouble.compareTo(civicPool.getCurrentDistance());
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

}