package com.example.tom.regensbad.Domain;

/**
 * Created by Tom on 24.08.2015.
 */
public class Weather {

    private String degrees;
    private String maxDegrees;
    private String minDegrees;
    private String humidity;
    private long sunrise;
    private long sunset;
    private String windSpeed;
    private String weatherDescription;
    private String weatherIcon;



    public Weather (String degrees, String maxDegrees, String minDegrees, String humidity, long sunrise, long sunset,
                    String windSpeed, String weatherDescription, String weatherIcon) {
        this.degrees = degrees;
        this.maxDegrees = maxDegrees;
        this.minDegrees = minDegrees;
        this.humidity = humidity;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.windSpeed = windSpeed;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
    }

    public String getDegrees () {
        return degrees;
    }

    public String getMaxDegrees () {
        return maxDegrees;
    }

    public String getMinDegrees () {
        return minDegrees;
    }

    public String getHumidity () {
        return humidity;
    }

    public long getSunrise () {
        return sunrise;
    }

    public long getSunset () {
        return sunset;
    }

    public String getWindSpeed () {
        return windSpeed;
    }

    public String getWeatherDescription () {
        return weatherDescription;
    }

    public String getweatherIcon () {
        return weatherIcon;
    }





}
