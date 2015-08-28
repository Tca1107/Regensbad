package com.example.tom.regensbad.Persistence;

import android.os.AsyncTask;
import android.util.Log;

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

    private void getDistanceInfo(String result) {
        Log.d("langes JSON", result);
    }


    public interface DistanceDataReceivedListener {
        public void onDataDistanceDataReceived();
    }
}
