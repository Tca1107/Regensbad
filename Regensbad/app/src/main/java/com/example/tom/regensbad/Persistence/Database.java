package com.example.tom.regensbad.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.example.tom.regensbad.Domain.CivicPool;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Tobias on 26.08.2015.
 */
public class Database {


        private static final String DATABASE_NAME = "civicpool.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "pooltasks";

        private static final String KEY_ID = "_id";

        //pooltasks
        private static final String KEY_NAME = "name";
        private static final String KEY_TYPE = "type";
        private static final String KEY_LATI = "lati";
        private static final String KEY_LONGI = "longi";
        private static final String KEY_PHONENUMBER = "phoneNumber";
        private static final String KEY_WEBSITE = "website";
        private static final String KEY_OPENTIME = "openTime";
        private static final String KEY_CLOSETIME = "closeTime";
        private static final String KEY_PICPATH = "picPath";
        private static final String KEY_CIVICID = "civicID";
        private static final String KEY_DISTANCE = "currentDistance";
        private static final String KEY_RATING = "currentRating";

        private static String REMOVE_ALL_ROWS = "1";
        private static final double DOUBLE_CUTTING_FACTOR = 100.0;

        //private static final int COLUMN_ID_INDEX = 0;
        private static final int COLUMN_NAME_INDEX = 1;
        private static final int COLUMN_TYPE_INDEX = 2;
        private static final int COLUMN_LATI_INDEX = 3;
        private static final int COLUMN_LONGI_INDEX = 4;
        private static final int COLUMN_PHONENUMBER_INDEX = 5;
        private static final int COLUMN_WEBSITE_INDEX = 6;
        private static final int COLUMN_OPENTIME_INDEX = 7;
        private static final int COLUMN_CLOSETIME_INDEX = 8;
        private static final int COLUMN_PICPATH_INDEX = 9;
        private static final int COLUMN_CIVICID_INDEX = 10;
        private static final int COLUMN_DISTANCE_INDEX = 11;
        private static final int COLUMN_RATING_INDEX = 12;


        private PoolDBOpenHelper dbHelper;
        private SQLiteDatabase db;

        public Database(Context context) {
            dbHelper = new PoolDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void open() {
            try {
                db = dbHelper.getWritableDatabase();
            } catch (SQLException e) {
                db = dbHelper.getReadableDatabase();
            }
        }

        public void close() {
            db.close();
        }




        public long deleteAllPoolItems () {
            return db.delete(DATABASE_TABLE, null, null);
        }

        public String getPicPath (int poolID) {
            Cursor cursor = db.query(DATABASE_TABLE, new String [] {KEY_CIVICID, KEY_PICPATH}, null, null, null, null, null);
            int idCheck = 0;
            if (cursor.moveToFirst()){
                do {
                    idCheck = cursor.getInt(0);
                    String picPath = cursor.getString(1);
                    if (idCheck == poolID) {
                        Log.d("Bild", picPath);
                        return picPath;
                    }
                } while (cursor.moveToNext());
            }
            return "Kein Bild gefunden!";
        }


        public long addCivicPoolItem(CivicPool civicPool) {
            ContentValues newPoolValues = new ContentValues();

            newPoolValues.put(KEY_NAME, civicPool.getName());
            newPoolValues.put(KEY_TYPE, civicPool.getType());
            newPoolValues.put(KEY_LATI, civicPool.getLati());
            newPoolValues.put(KEY_LONGI, civicPool.getLongi());
            newPoolValues.put(KEY_PHONENUMBER, civicPool.getPhoneNumber());
            newPoolValues.put(KEY_WEBSITE, civicPool.getWebsite());
            newPoolValues.put(KEY_OPENTIME, civicPool.getOpenTime());
            newPoolValues.put(KEY_CLOSETIME, civicPool.getCloseTime());
            newPoolValues.put(KEY_PICPATH, civicPool.getPicPath());
            newPoolValues.put(KEY_CIVICID, civicPool.getID());
            newPoolValues.put(KEY_DISTANCE, civicPool.getCurrentDistance());
            newPoolValues.put(KEY_RATING, civicPool.getCurrentRating());
            Log.d("currentDistance1117", String.valueOf(civicPool.getCurrentDistance()));
            return db.insert(DATABASE_TABLE, null, newPoolValues);
        }


        public CivicPool getPoolItem(int poolItemID) {
            Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_TYPE, KEY_LATI, KEY_LONGI, KEY_PHONENUMBER, KEY_WEBSITE, KEY_OPENTIME, KEY_CLOSETIME, KEY_PICPATH, KEY_CIVICID, KEY_DISTANCE, KEY_RATING}, KEY_CIVICID + "=" + poolItemID, null, null, null, null, null);

            CivicPool result;
            if (cursor.moveToFirst()) {
                String name = cursor.getString(COLUMN_NAME_INDEX);
                String type = cursor.getString(COLUMN_TYPE_INDEX);
                String lati = cursor.getString(COLUMN_LATI_INDEX);
                String longi = cursor.getString(COLUMN_LONGI_INDEX);
                String phoneNumber = cursor.getString(COLUMN_PHONENUMBER_INDEX);
                String website = cursor.getString(COLUMN_WEBSITE_INDEX);
                String openTime = cursor.getString(COLUMN_OPENTIME_INDEX);
                String closeTime = cursor.getString(COLUMN_CLOSETIME_INDEX);
                String picPath = cursor.getString(COLUMN_PICPATH_INDEX);
                String civicID = cursor.getString(COLUMN_CIVICID_INDEX);
                double currentDistance = cursor.getDouble(COLUMN_DISTANCE_INDEX);
                float currentRating = cursor.getFloat(COLUMN_RATING_INDEX);

                result = new CivicPool(name, type, Double.parseDouble(lati), Double.parseDouble(longi), phoneNumber, website, openTime, closeTime, picPath, Integer.parseInt(civicID), currentDistance, currentRating);
                return result;
            } else {
                return null;
            }
        }


        public long updatePhoneNumber(String poolItemID, int phoneNumber) {
            ContentValues newPoolValues = new ContentValues();
            newPoolValues.put(KEY_PHONENUMBER, phoneNumber);

            long phoneNumberUpdate = db.update(DATABASE_TABLE, newPoolValues, KEY_NAME + " = '" + poolItemID + "'", null);
            return phoneNumberUpdate;
        }

        public long updateopenTime(String poolItemID, double openTime) {
            ContentValues newPoolValues = new ContentValues();
            String openTimeString = Double.toString(openTime);
            newPoolValues.put(KEY_OPENTIME, openTimeString);

            long openTimeUpdate = db.update(DATABASE_TABLE, newPoolValues, KEY_NAME + " = '" + poolItemID + "'", null);
            return openTimeUpdate;
        }

        public long updateCloseTime(String poolItemID, double closeTime) {
            ContentValues newPoolValues = new ContentValues();
            String closeTimeString = Double.toString(closeTime);
            newPoolValues.put(KEY_CLOSETIME, closeTimeString);

            long closeTimeUpdate = db.update(DATABASE_TABLE, newPoolValues, KEY_NAME + " = '" + poolItemID + "'", null);
            return closeTimeUpdate;
        }


        public ArrayList<CivicPool> getAllPoolItems() {
            ArrayList<CivicPool> poolItems = new ArrayList<CivicPool>();
            Cursor cursor = db.query(DATABASE_TABLE, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(COLUMN_NAME_INDEX);
                    String type = cursor.getString(COLUMN_TYPE_INDEX);
                    String lati = cursor.getString(COLUMN_LATI_INDEX);
                    String longi = cursor.getString(COLUMN_LONGI_INDEX);
                    String phonenumber = cursor.getString(COLUMN_PHONENUMBER_INDEX);
                    String website = cursor.getString(COLUMN_WEBSITE_INDEX);
                    String opentime = cursor.getString(COLUMN_OPENTIME_INDEX);
                    String closetime = cursor.getString(COLUMN_CLOSETIME_INDEX);
                    String picpath = cursor.getString(COLUMN_PICPATH_INDEX);
                    String civicID = cursor.getString(COLUMN_CIVICID_INDEX);
                    float currentDistance = cursor.getFloat(COLUMN_DISTANCE_INDEX);
                    float currentRating = cursor.getFloat(COLUMN_RATING_INDEX);

                    Log.d("getAllPoolItems", String.valueOf(currentDistance));
                    // TEST


                    poolItems.add(new CivicPool(name, type, Double.parseDouble(lati), Double.parseDouble(longi), phonenumber, website, opentime, closetime, picpath, Integer.parseInt(civicID), currentDistance, currentRating));

                } while (cursor.moveToNext());
            }
            return poolItems;

        }

    private class PoolDBOpenHelper extends SQLiteOpenHelper {
            private static final String DATABASE_CREATE = "create table "
                    + DATABASE_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_NAME + " text, "
                    + KEY_TYPE + " text, "
                    + KEY_LATI + " text, "
                    + KEY_LONGI + " text, "
                    + KEY_PHONENUMBER + " text, "
                    + KEY_WEBSITE + " text, "
                    + KEY_OPENTIME + " text, "
                    + KEY_CLOSETIME + " text, "
                    + KEY_PICPATH + " text not null, "
                    + KEY_CIVICID + " integer, "
                    + KEY_DISTANCE + " float, "
                    + KEY_RATING + " float);";

            public PoolDBOpenHelper(Context c, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
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

