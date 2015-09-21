package com.example.tom.regensbad.HelperClasses;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Tom on 21.09.2015.
 */

/* This class was created with the help of: http://stackoverflow.com/questions/30135858/parse-error-parseenablelocaldatastorecontext-must-be-invoked-before-parse*
   It was created to prevent the application from crashing, if it is closed and opened again right away. A respective line was added to the AndroidManifest.xml.
 */

public class ParseInitializationApplication extends Application {

    /* Constants that are needed to initialize parse.com as the backend of the application. */
    private static final String PARSE_COM_APPLICATION_ID = "lrveDDA87qqqf7FfRTjPfOFdZ0DrVLEypfg6dDql";
    private static final String PARSE_COM_CLIENT_ID = "JYvtqFzgpOHVq9OTn8yKcJdC7xM7eRe3hciBhVh8";


    @Override
    public void onCreate () {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_COM_APPLICATION_ID, PARSE_COM_CLIENT_ID);
    }




}
