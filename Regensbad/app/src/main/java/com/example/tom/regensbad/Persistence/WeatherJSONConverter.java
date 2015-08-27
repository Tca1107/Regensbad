package com.example.tom.regensbad.Persistence;

import android.util.Log;

import com.example.tom.regensbad.Domain.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 26.08.2015.
 */
public class WeatherJSONConverter {

    private static final String MAIN ="main";
    private static final String DEGREES = "temp";
    private static final String WEATHER = "weather";
    private static final String DESCRIPTION = "description";
    private static final String ICON_CODE = "id";
    private static final String SYS = "sys";
    private static final String SUNRISE = "sunrise";
    private static final String SUNSET = "sunset";
    private static final String MAX_DEGREES = "temp_max";
    private static final String MIN_DEGREES = "temp_min";
    private static final String HUMIDITY = "humidity";
    private static final String WIND = "wind";
    private static final String WIND_SPEED = "speed";

    private String JSONResult;
    private Weather weatherToReturn;

    public WeatherJSONConverter (String JSONResult) {
        this.JSONResult = JSONResult;
    }


    public Weather convertJSONToWeatherObject () {
        try {
            JSONObject jsonObject = new JSONObject(JSONResult);
            JSONObject main = jsonObject.getJSONObject(MAIN);
            String degrees = main.getString(DEGREES);
            String maxDegrees = main.getString(MAX_DEGREES);
            String minDegrees = main.getString(MIN_DEGREES);
            String humidity = main.getString(HUMIDITY);
            JSONObject sys = jsonObject.getJSONObject(SYS);
            long sunrise = sys.getLong(SUNRISE);
            long sunset = sys.getLong(SUNSET);
            JSONObject wind = jsonObject.getJSONObject(WIND);
            String windSpeed = wind.getString(WIND_SPEED);
            JSONArray weather = jsonObject.getJSONArray(WEATHER);
            JSONObject weatherObject = weather.getJSONObject(0);
            String weatherDescription = weatherObject.getString(DESCRIPTION);
            String weatherIcon = weatherObject.getString(ICON_CODE);

            //Logs for debugging
            Log.d("windgeschwind", windSpeed);
            Log.d("humidity", humidity);
            Log.d("minDegrees", minDegrees);
            Log.d("maxDegrees", maxDegrees);
            Log.d("sunrise", String.valueOf(sunrise));
            Log.d("sunset", String.valueOf(sunset));
            Log.d("degrees:", degrees);
            Log.d("weatherDescription", weatherDescription);
            Log.d("weatherIcon", weatherIcon);

            weatherToReturn = new Weather (degrees, maxDegrees, minDegrees, humidity, sunrise, sunset,
                    windSpeed, weatherDescription, weatherIcon);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        return weatherToReturn;
    }




}
