package com.example.tom.regensbad.Domain;


import java.net.URI;

/**
 * Created by Sebastian on 26.08.2015.
 */
public class CivicPool {

    private String name;
    private String type;
    private double lati;
    private double longi;
    private String phoneNumber;
    private String website;
    private int openTime;
    private int closeTime;
    private String picPath;

    public CivicPool (String name ,String type, double lati, double longi, String phoneNumber, String website, int openTime, int closeTime, String picPath){
        this.name = name;
        this.type = type;
        this.lati = lati;
        this.longi = longi;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.picPath = picPath;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public double getLati(){
        return lati;
    }

    public double getLongi(){
        return longi;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getPhoneNumberAsString(){
        String numberString = String.valueOf(phoneNumber);
        return numberString;
    }

    public String getWebsite(){
        return website;
    }

    public String getWebsiteAsString(){
        String webstieString = website.toString();
        return webstieString;
    }

    public int getOpenTime(){
        return openTime;
    }

    public int getCloseTime(){
        return closeTime;
    }

    public String getPicPath(){
        return picPath;
    }


}
