package com.example.tom.regensbad.Domain;

/**
 * Created by Tom on 24.08.2015.
 */
public class Weather {

    private String degrees;
    private String weatherDescription;
    private String weatherIcon;

    public Weather (String degrees, String weatherDescription, String weatherIcon) {
        this.degrees = degrees;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
    }

    public String getDegrees () {
        return degrees;
    }

    public String getWeatherDescription () {
        return weatherDescription;
    }

    public String getweatherIcon () {
        return weatherIcon;
    }



}
