package com.example.tom.regensbad.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tom.regensbad.R;

/**
 * Created by Tobi on 28.08.15.
 */

    public class LocationManager extends Activity implements LocationUpdater.OnLocationUpdateReceivedListener {
        private TextView locationText;

        // Properties for location updates
        private static final int FIX_UPDATE_TIME = 500; // milliseconds
        private static final int FIX_UPDATE_DISTANCE = 5; // meters

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //setupUIComponents();
            setupLocationUpdates();
        }

        /*private void setupUIComponents() {
            setContentView(R.layout.main);
            // Get a reference to the TextView that displays location updates
            locationText = (TextView) findViewById(R.id.myLocationText);
        }*/

        private void setupLocationUpdates() {
            LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
            locationUpdater.setLocationUpdateListener(this);
            locationUpdater.requestLocationUpdates();
        }

        @Override
        public void onFormattedLocationReceived(String formattedLocation) {
            locationText.setText(formattedLocation);
        }
    }

