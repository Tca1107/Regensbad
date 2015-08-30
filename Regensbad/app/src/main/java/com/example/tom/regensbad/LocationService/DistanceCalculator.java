package com.example.tom.regensbad.LocationService;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sebastian on 28.08.2015.
 */
public class DistanceCalculator implements LocationUpdater.OnLocationUpdateReceivedListener, DistanceDataProvider.DistanceDataReceivedListener {

    // Properties for location updates
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters

    private double userLat;
    private double userLong;

    private double distance;

    private Database db;

    private Context context;

    private DistanceDataProvider distanceDataProvider;



    public DistanceCalculator(Context context){
        this.context = context;
        initDB();
        initializeLocationService();
    }

    private void initDB() {
        db = new Database(context);
        db.open();
    }

    private void initializeLocationService() {
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, context);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
    }

    public double calculateDistanceToPool(int poolID){
        System.out.println("calc: "+poolID);
        CivicPool pool = db.getPoolItem(poolID);
        System.out.println("name: "+pool.getName());
        double poolLat = pool.getLati();
        double poolLong = pool.getLongi();

        Location userLocation = new Location("UserLoc");
        userLocation.setLatitude(userLat);
        userLocation.setLongitude(userLong);

        Location poolLocation = new Location("Pool");
        poolLocation.setLatitude(poolLat);
        poolLocation.setLongitude(poolLong);


        float[]dist=new float[1];
        Location.distanceBetween(userLat, userLong, poolLat, poolLong, dist);

        return getDistanceInfo(userLat, userLong, pool.getName());
    }

    //From: http://stackoverflow.com/questions/14618016/distancebetween-returns-inaccurate-result
    private double getDistanceInfo(double lat1, double lng1, String destinationAddress) {
        distanceDataProvider = new DistanceDataProvider();
        distanceDataProvider.setOnDistanceDataReceivedListener(this);
        distanceDataProvider.execute("http://maps.googleapis.com/maps/api/directions/json?origin=49.0312,12.1022&destination=GuggenbergerSee&mode=driving&sensor=false");




        /*StringBuilder stringBuilder = new StringBuilder();
        Double dist = 0.0;
        try {

            destinationAddress = destinationAddress.replaceAll(" ","%20");
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 + "&destination=" + destinationAddress + "&mode=driving&sensor=false";

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
        return 77;
    }

    @Override
    public void onFormattedLocationReceived(String formattedLocation) {
        int separator = 0;
        for (int i = 0; i < formattedLocation.length(); i++) {
            char charToCheck = formattedLocation.charAt(i);
            if (charToCheck == ','){
                separator = i;
            }
        }
        String latString = formattedLocation.substring(0, separator);
        String longString = formattedLocation.substring(separator + 1);
        Log.d("userLat", latString);
        userLat = Double.parseDouble(latString);
        userLong = Double.parseDouble(longString);
    }

    @Override
    public void onDataDistanceDataReceived() {

    }
}
