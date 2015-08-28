package com.example.tom.regensbad.Persistence;

import android.os.AsyncTask;

/**
 * Created by Sebastian on 28.08.2015.
 */
public class DistanceDataProvider extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    public interface DistanceDataReceivedListener {
        public void onDataDistanceDataReceived();
    }
}
