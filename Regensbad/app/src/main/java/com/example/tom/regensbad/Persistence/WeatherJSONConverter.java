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
            JSONArray weather = jsonObject.getJSONArray(WEATHER);
            JSONObject weatherObject = weather.getJSONObject(0);
            String weatherDescription = weatherObject.getString(DESCRIPTION);
            String weatherIcon = weatherObject.getString(ICON_CODE);
            Log.d("degrees:", degrees);
            Log.d("weatherDescription", weatherDescription);
            Log.d("weatherIcon", weatherIcon);
            weatherToReturn = new Weather (degrees, weatherDescription, weatherIcon);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        return weatherToReturn;
    }




}
