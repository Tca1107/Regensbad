package com.example.tom.regensbad.LocationService;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Tobi on 28.08.15.
 */

    public class LocationUpdater implements LocationListener {

        private String locService;
        private int time;
        private int distance;
        private static final String provider = android.location.LocationManager.GPS_PROVIDER;

        private Context context;

        private static final String LONGITUDE = "Länge";
        private static final String LATITUDE = "Breite: ";
        private static final String ALTITUDE = "Höhe: ";
        private static final String GROUND_SPEED = "Geschwindigkeit";

        private static final String NO_LOCATION_FOUND = "No location found...";
        private static final String NEW_LINE_CHAR = "\n";

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

            android.location.LocationManager locationManager = (android.location.LocationManager)context.getSystemService(locService);

            Location location = locationManager.getLastKnownLocation(provider);
            // update location now with last known position from GPS provider
            String formattedLocation = createFormattedLocation(location);
            publishLocationUpdate(formattedLocation);

            // triggers location updates at the specified time and when distance of
            // device has changed (distance)
            // myLocationListener is registered ==> Callback functions are called
            // when location change events occur
            locationManager.requestLocationUpdates(provider, time, distance,
                    this);
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
            String latLongString;

            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                double alt = location.getAltitude();
                double speed = location.getSpeed() * 3.6;

                latLongString = LONGITUDE + ": " + lat + NEW_LINE_CHAR + LATITUDE + lng + NEW_LINE_CHAR
                        + ALTITUDE + alt + "m" + NEW_LINE_CHAR + GROUND_SPEED + ": " + speed + "km/h";
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

