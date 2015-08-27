package com.example.tom.regensbad.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

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

        public static final String KEY_ID = "_id";

        //pooltasks
        public static final String KEY_NAME = "name";
        public static final String KEY_TYPE = "type";
        public static final String KEY_LATI = "lati";
        public static final String KEY_LONGI = "longi";
        public static final String KEY_PHONENUMBER = "phoneNumber";
        public static final String KEY_WEBSITE = "website";
        public static final String KEY_OPENTIME = "openTime";
        public static final String KEY_CLOSETIME = "closeTime";
        public static final String KEY_PICPATH = "picPath";


        //public static final int COLUMN_ID_INDEX = 0;
        public static final int COLUMN_NAME_INDEX = 1;
        public static final int COLUMN_TYPE_INDEX = 2;
        public static final int COLUMN_LATI_INDEX = 3;
        public static final int COLUMN_LONGI_INDEX = 4;
        public static final int COLUMN_PHONENUMBER_INDEX = 5;
        public static final int COLUMN_WEBSITE_INDEX = 6;
        public static final int COLUMN_OPENTIME_INDEX = 7;
        public static final int COLUMN_CLOSETIME_INDEX = 8;
        public static final int COLUMN_PICPATH_INDEX = 9;


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


        public long addCivicPoolItem(CivicPool civicPool) {
            ContentValues newPoolValues = new ContentValues();

            newPoolValues.put(KEY_NAME, civicPool.getName());
            newPoolValues.put(KEY_TYPE, civicPool.getType());
            newPoolValues.put(KEY_LATI, civicPool.getLati());
            newPoolValues.put(KEY_LONGI, civicPool.getLongi());
            newPoolValues.put(KEY_PHONENUMBER, civicPool.getPhoneNumberAsString());
            newPoolValues.put(KEY_WEBSITE, civicPool.getWebsiteAsString());
            newPoolValues.put(KEY_OPENTIME, civicPool.getOpenTime());
            newPoolValues.put(KEY_CLOSETIME, civicPool.getCloseTime());
            newPoolValues.put(KEY_PICPATH, civicPool.getPicPath());


            return db.insert(DATABASE_TABLE, null, newPoolValues);
        }

        public CivicPool getPoolItem(String poolItemID) {
            Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_TYPE, KEY_LATI, KEY_LONGI, KEY_PHONENUMBER, KEY_WEBSITE, KEY_OPENTIME, KEY_CLOSETIME, KEY_PICPATH}, KEY_NAME + "=" + poolItemID, null, null, null, null, null);

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
                result = new CivicPool(name, type, Double.parseDouble(lati), Double.parseDouble(longi), phoneNumber, URI.create(website), Double.parseDouble(openTime), Double.parseDouble(closeTime), picPath);
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
            ArrayList<CivicPool> poolItems = new ArrayList<>();
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
                    poolItems.add(new CivicPool(name, type, Double.parseDouble(lati), Double.parseDouble(longi), phonenumber, URI.create(website), Double.parseDouble(opentime), Double.parseDouble(closetime), picpath));

                } while (cursor.moveToNext());
            }
            return poolItems;

        }


        public int deletePoolItem(CivicPool singleItem) {
            String whereClause = KEY_NAME + " = '" + singleItem.getName() + "'";
            db.delete(DATABASE_TABLE, whereClause, null);
            return 0;
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
                    + KEY_PICPATH + " text not null);";

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

