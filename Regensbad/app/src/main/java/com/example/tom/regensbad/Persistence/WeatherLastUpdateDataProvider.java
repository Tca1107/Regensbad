package com.example.tom.regensbad.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tom.regensbad.Domain.Weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Tom on 27.08.2015.
 */


public class WeatherLastUpdateDataProvider {

    private static final String DATABASE_NAME = "weatherLastUpdate.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "weatherLastUpdate";

    private static final String KEY_DEGREES = "degrees";
    private static final String KEY_MAX_DEGREES = "max_degrees";
    private static final String KEY_MIN_DEGREES = "mind_degrees";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_SUNRISE = "sunrise";
    private static final String KEY_SUNSET = "sunset";
    private static final String KEY_WINDSPEED = "windspeed";
    private static final String KEY_WEATHER_DESCRIPTION = "waetherDescription";
    private static final String KEY_WEATHER_ICON = "waetherIcon";
    private static final String KEY_LATEST_UPDATE_TIME = "latestUpdate";

    //private static final int ID_COLUMN = 0;
    private static final int DEGREES_COLUMN = 0;
    private static final int MAX_DEGREES_COLUMN = 1;
    private static final int MIN_DEGREES_COLUMN = 2;
    private static final int HUMIDITY_COLUMN = 3;
    private static final int SUNRISE_COLUMN = 4;
    private static final int SUNSET_COLUMN = 5;
    private static final int WINDSPEED_COLUMN = 6;
    private static final int WEATHER_DESCRIPTION_COLUMN = 7;
    private static final int WEATHER_ICON_COLUMN = 8;
    private static final int LATEST_UPDATE_TIME_COLUMN = 0;

    private static final String KEY_ID = "_id";
    private static final int LATEST_UPDATE_ID = 1;
    private static final int TIME_DIVISION_FACTOR = 1000;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String TIME_ZONE = "CET"; // Central European Time
    private static final int SUBSTRING_START = 11;
    private static final int SUBSTRING_END = 16;
    private static final String REMOVE_ALL_ROWS = "1";


    private SQLiteDatabase database;
    private WeatherLastUpdateDataProviderOpenHelper openHelper;


    public WeatherLastUpdateDataProvider (Context context) {
        openHelper = new WeatherLastUpdateDataProviderOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        try {
            database = openHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = openHelper.getReadableDatabase();
        }
    }

    public void close() {
        database.close();
    }

    public String getLatestUpdateTime() {
        Cursor cursor = database.query(DATABASE_TABLE, new String [] {KEY_LATEST_UPDATE_TIME}, null, null, null, null, null);
        if (cursor.moveToFirst()){
            String latestUpdateTime = cursor.getString(LATEST_UPDATE_TIME_COLUMN);
            return latestUpdateTime;
        } else {
            return "";
        }
    }


    public Weather getLatestUpdate () {
        Cursor cursor = database.query(DATABASE_TABLE, new String [] {KEY_DEGREES, KEY_MAX_DEGREES, KEY_MIN_DEGREES, KEY_HUMIDITY,
        KEY_SUNRISE, KEY_SUNSET, KEY_WINDSPEED, KEY_WEATHER_DESCRIPTION, KEY_WEATHER_ICON}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String degrees = cursor.getString(DEGREES_COLUMN);
            String maxDegrees = cursor.getString(MAX_DEGREES_COLUMN);
            String minDegrees = cursor.getString(MIN_DEGREES_COLUMN);
            String humidity = cursor.getString(HUMIDITY_COLUMN);
            long sunrise = cursor.getLong(SUNRISE_COLUMN);
            long sunset = cursor.getLong(SUNSET_COLUMN);
            String windspeed = cursor.getString(WINDSPEED_COLUMN);
            String weatherDescription = cursor.getString(WEATHER_DESCRIPTION_COLUMN);
            String weatherIcon = cursor.getString(WEATHER_ICON_COLUMN);
            Weather weather = new Weather (degrees, maxDegrees, minDegrees, humidity, sunrise, sunset, windspeed, weatherDescription, weatherIcon);
            return weather;
        } else {
            return null;
        }
    }


    public long addLatestUpdate (Weather weather) {
        ContentValues lastUpdateValues = new ContentValues();
        lastUpdateValues.put(KEY_DEGREES, weather.getDegrees());
        lastUpdateValues.put(KEY_MAX_DEGREES, weather.getMaxDegrees());
        lastUpdateValues.put(KEY_MIN_DEGREES, weather.getMinDegrees());
        lastUpdateValues.put(KEY_HUMIDITY, weather.getHumidity());
        lastUpdateValues.put(KEY_SUNRISE, weather.getSunrise());
        lastUpdateValues.put(KEY_SUNSET, weather.getSunset());
        lastUpdateValues.put(KEY_WINDSPEED, weather.getWindSpeed());
        lastUpdateValues.put(KEY_WEATHER_DESCRIPTION, weather.getWeatherDescription());
        lastUpdateValues.put(KEY_WEATHER_ICON, weather.getweatherIcon());
        String currentTime = fetchCurrentTime();
        lastUpdateValues.put(KEY_LATEST_UPDATE_TIME, currentTime);
        return database.insert(DATABASE_TABLE, null, lastUpdateValues);
    }

    /* This method was written using the resource http://stackoverflow.com/questions/8077530/android-get-current-timestamp
    * as a guideline. It gets the current time of the system. */
    private String fetchCurrentTime () {
        long time = System.currentTimeMillis();
        String formattedTime = formatTimeString(time);
        String formattedTimeOnlyHoursAndMinutes = formattedTime.substring(SUBSTRING_START, SUBSTRING_END);
        return formattedTimeOnlyHoursAndMinutes;
    }


    /* This method was written using the resource http://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java
    * as a guideline. It formats the unix time string into a human-readable format. */
    private String formatTimeString(long time) {
        Date todayDate = new Date (time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        String dateInFormat = simpleDateFormat.format(todayDate);
        return dateInFormat;
    }

    public long deleteLatestUpdate () {
       // String whereClause = KEY_ID + " = '" + LATEST_UPDATE_ID + "'";
        return database.delete(DATABASE_TABLE, REMOVE_ALL_ROWS, null);
    }




    private class WeatherLastUpdateDataProviderOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE
                + " (" + KEY_ID + " integer primary key, "
                + KEY_DEGREES + " text not null, "
                + KEY_MAX_DEGREES + " text not null, "
                + KEY_MIN_DEGREES + " text not null, "
                + KEY_HUMIDITY + " text not null, "
                + KEY_SUNRISE + " text not null, "
                + KEY_SUNSET + " text not null, "
                + KEY_WINDSPEED + " text not null, "
                + KEY_WEATHER_DESCRIPTION + " text not null, "
                + KEY_WEATHER_ICON + " text not null, "
                + KEY_LATEST_UPDATE_TIME + " text not null);";

        public WeatherLastUpdateDataProviderOpenHelper(Context c, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
            super(c, dbname, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
