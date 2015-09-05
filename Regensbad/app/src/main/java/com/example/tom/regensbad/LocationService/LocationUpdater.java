package com.example.tom.regensbad.LocationService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.tom.regensbad.Activities.AllCivicPoolsActivity;
import com.example.tom.regensbad.R;

/**
 * Created by Tobi on 28.08.15.
 */

    public class LocationUpdater implements LocationListener {

        private String locService;
        private int time;
        private int distance;
        private static final String PROVIDER = LocationManager.GPS_PROVIDER;

        private Context context;

        private static final String LONGITUDE = "Länge";
        private static final String LATITUDE = "Breite: ";
        private static final String ALTITUDE = "Höhe: ";
        private static final String GROUND_SPEED = "Geschwindigkeit";

        private static final String NO_LOCATION_FOUND = "No location found...";
        private static final String NEW_LINE_CHAR = "\n";
        private static final String REGENSBURG_MAIN_STATION_GPS = "49.010259,12.100722";

        private Location location;

        private OnLocationUpdateReceivedListener onLocationUpdateReceivedListener;

        public LocationUpdater(String locService, int time, int distance, Context context) {
            this.locService = locService;
            this.time = time;
            this.distance = distance;
            this.context = context;
        }

        public void setLocationUpdateListener(OnLocationUpdateReceivedListener onLocationUpdateReceiver) {
            this.onLocationUpdateReceivedListener = onLocationUpdateReceiver;
        }

        public void requestLocationUpdates() {
            //Log.d("Requesting location updates in location updater");
            // copied from respective power point slide from the lecture
            String locService = Context.LOCATION_SERVICE;
            LocationManager locationManager = (LocationManager)context.getSystemService(locService);
            Log.d("LOCATIONMANAGER", String.valueOf(locationManager));
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            String bestProvider = locationManager.getBestProvider(criteria,
                    true);
            //android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(locService);
            // String provider = LocationManager.GPS_PROVIDER;;
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Log.d("LOCATIONLOCATION", String.valueOf(location));
            if (location != null) {
                Log.d("LastKnownLoc", String.valueOf(location));
                // update location now with last known position from GPS provider
                String formattedLocation = createFormattedLocation(location);
                Log.d("FORAMTEED", formattedLocation);
                publishLocationUpdate(formattedLocation);

                // triggers location updates at the specified time and when distance of
                // device has changed (distance)
                // myLocationListener is registered ==> Callback functions are called
                // when location change events occur
                locationManager.requestLocationUpdates(bestProvider, time, distance,
                        this);
            } else {
                Log.d("REGENSBURGER", "BAHNHOF");
                publishLocationUpdate(REGENSBURG_MAIN_STATION_GPS);

                // HIER UNBEDINGT DEN USER NOCH WISSEN LASSEN, DASS ES VOM REGENSBURGER BAHNHOF ASU GEMESSEN WIRD!!!!


            }
        }





    private void publishLocationUpdate(String formattedLocation) {
            if (onLocationUpdateReceivedListener != null) {
                //Log.d("Publishing location to Activity");
                onLocationUpdateReceivedListener.onFormattedLocationReceived(formattedLocation);
            } else {
                Log.d("Location app: ", "Error, location update listener not set.");
            }
        }

        private String createFormattedLocation(Location location) {
            Log.d("LOCATION", String.valueOf(location));
            String latLongString;

            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                latLongString = lat + "," + lng;
            } else {
                latLongString = NO_LOCATION_FOUND;
            }
            return latLongString;
        }


        @Override
        public void onLocationChanged(Location location) {
            String formattedLocation = createFormattedLocation(location);
            publishLocationUpdate(formattedLocation);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }


        public interface OnLocationUpdateReceivedListener {
            void onFormattedLocationReceived(String formattedLocation);
        }
    }

