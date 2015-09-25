package com.example.tom.regensbad.Persistence;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sebastian on 28.08.2015.
 */
public class DistanceDataProvider extends AsyncTask<String, Integer, String>{

    private static final String JSON_ROUTES = "routes";
    private static final String JSON_LEGS = "legs";
    private static final String JSON_DISTANCE = "distance";
    private static final String JSON_TEXT = "text";
    private static final String JSON_REPLACE_ALL = "[^\\.0123456789]";

    private DistanceDataReceivedListener distanceDataReceivedListener;


    public void setOnDistanceDataReceivedListener (DistanceDataReceivedListener distanceDataReceivedListener) {
        this.distanceDataReceivedListener = distanceDataReceivedListener;
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
        getDistanceInfo(result);


    }

    // The code for this method was taken from http://stackoverflow.com/questions/14618016/distancebetween-returns-inaccurate-result .
    private void getDistanceInfo(String result) {
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray routes = jsonObject.getJSONArray(JSON_ROUTES);
            JSONObject routesTwo = routes.getJSONObject(0);
            JSONArray legs = routesTwo.getJSONArray(JSON_LEGS);
            JSONObject steps = legs.getJSONObject(0);
            JSONObject distance = steps.getJSONObject(JSON_DISTANCE);
            double dist = Double.parseDouble(distance.getString(JSON_TEXT).replaceAll(JSON_REPLACE_ALL,""));
            distanceDataReceivedListener.onDataDistanceDataReceived(dist);
        }catch(JSONException e){
            e.printStackTrace();
        }

    }


    /* Interface that was written for the sake of being able to user the observer pattern.
    * This informs the listener activities when the download is finished and the distance data is available. */

    public interface DistanceDataReceivedListener {
        public void onDataDistanceDataReceived(double dist);
    }
}
