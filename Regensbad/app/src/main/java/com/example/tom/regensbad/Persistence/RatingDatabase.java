package com.example.tom.regensbad.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by Tobi on 05.09.15.
 */
public class RatingDatabase {

    private static final String DATABASE_NAME = "rating.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "ratingtasks";

    private static final String KEY_ID = "_id";

    //ratingtasks
    private static final String KEY_CIVICID = "civicId";
    private static final String KEY_RATING = "rating";

    private static final int COLUMN_CIVICID_INDEX = 1;
    private static final int COLUMN_RATING_INDEX = 2;


    private RatingDBOpenHelper dbHelper;
    private SQLiteDatabase db;

    public RatingDatabase(Context context) {
        dbHelper = new RatingDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public long clearRatingDatabase () {
        return db.delete(DATABASE_TABLE, null, null);
    }


    public long saveRatingToDatabase (int poolID, int rating) {
        ContentValues ratingValue = new ContentValues();
        ratingValue.put(KEY_CIVICID, poolID);
        ratingValue.put(KEY_RATING, rating);
        return db.insert(DATABASE_TABLE, null, ratingValue);
    }


    public ArrayList<Integer> getAllCivicPoolIDs () {
        ArrayList<Integer> allCivicIDs = new ArrayList<Integer>();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int civicID = cursor.getInt(0);
                allCivicIDs.add(civicID);
            } while (cursor.moveToNext());
        }
        return allCivicIDs;
    }

    /*
    public ArrayList getRatings (int poolID) {
        Cursor cursor = db.query(DATABASE_TABLE, new String [] {KEY_CIVICID, KEY_RATING}, null, null, null, null, null);
        int idCheck = 0;
        if (cursor.moveToFirst()){
            do {
                idCheck = cursor.getInt(0);
                int rating = cursor.getInt(1);
                if (idCheck == poolID) {
                    return rating;
                }
            } while (cursor.moveToNext());
        }
        return 0;
    } */




        private class RatingDBOpenHelper extends SQLiteOpenHelper {
            private static final String DATABASE_CREATE = "create table "
                    + DATABASE_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_CIVICID + " integer, "
                    + KEY_RATING + " integer);";

            public RatingDBOpenHelper(Context c, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
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
