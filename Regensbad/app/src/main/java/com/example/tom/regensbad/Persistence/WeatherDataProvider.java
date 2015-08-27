package com.example.tom.regensbad.Persistence;

import android.os.AsyncTask;

import com.example.tom.regensbad.Domain.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Tom on 24.08.2015.
 */

/* This class as well as the corresponding layout-xml-file "activity_home_screen.xml" were created using
       "http://code.tutsplus.com/tutorials/create-a-weather-app-on-android--cms-21587" as a guideline. */

public class WeatherDataProvider extends AsyncTask<String, Integer, String> {

    private WeatherJSONConverter weatherJSONConverter;
    private WeatherDataReceivedListener weatherDataReceivedListener;

    public void setOnWeatherDataReceivedListener (WeatherDataReceivedListener weatherDataReceivedListener) {
        this.weatherDataReceivedListener = weatherDataReceivedListener;
    }


       /* For the following methods the file "09 Bundesligatabelle: Lösung" on GRIPS was used as a guideline. */
       @Override
       protected String doInBackground(String... params) {
              String downloadJSONString = "";
              try {
                     URL url = new URL (params[0]);
                     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                     InputStream inputStream = connection.getInputStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                     String line;
                     while ((line = bufferedReader.readLine()) != null) {
                            downloadJSONString += line;
                     }

                     bufferedReader.close();
                     inputStream.close();
                     connection.disconnect();

              } catch (IOException exception) {
                     exception.printStackTrace();
              }

              return downloadJSONString;
       }



       @Override
       protected void onPostExecute (String result) {
           super.onPostExecute(result);
           weatherJSONConverter = new WeatherJSONConverter(result);
           Weather weather = weatherJSONConverter.convertJSONToWeatherObject();
           weatherDataReceivedListener.onDataWeatherDataReceived(weather);
       }


    /* Needed for Observer Pattern. */
    public interface WeatherDataReceivedListener {
        public void onDataWeatherDataReceived(Weather weather);
    }
}

