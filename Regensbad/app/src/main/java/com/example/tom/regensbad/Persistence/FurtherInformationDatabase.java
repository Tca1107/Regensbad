package com.example.tom.regensbad.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tom on 17.09.2015.
 */
public class FurtherInformationDatabase {

    private static final String DATABASE_NAME = "furtherInformation.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "furtherInformation";

    private static final String KEY_CORRESPONDING_CIVIC_ID = "correspondingCivicID";
    private static final String KEY_SPORTS = "sports";
    private static final String KEY_DAY_TICKET = "dayTicket";

    private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN_DAY_TICKET_INDEX = 1;
    private static final int COLUMN_SPORTS_INDEX = 1;


    private FurtherInformationDBOpenHelper dbOpenHelper;
    private SQLiteDatabase database;


    public FurtherInformationDatabase (Context context) {
        dbOpenHelper = new FurtherInformationDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        try {
            database = dbOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close() {
        database.close();
    }

    public long deleteAllFurtherInformationItems () {
        return database.delete(DATABASE_TABLE, null, null);
    }

    public long addFurtherInformationItem (int correspondingCivicID, String dayTicket, String sports) {
        ContentValues values = new ContentValues();
        values.put(KEY_CORRESPONDING_CIVIC_ID, correspondingCivicID);
        values.put(KEY_DAY_TICKET, dayTicket);
        values.put(KEY_SPORTS, sports);
        return database.insert(DATABASE_TABLE, null, values);
    }

    public String getDayTicket (int correspondingCivicID) {
        Cursor cursor = database.query(DATABASE_TABLE, new String [] {KEY_CORRESPONDING_CIVIC_ID, KEY_DAY_TICKET}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(COLUMN_ID_INDEX) == correspondingCivicID) {
                    Log.d("DAY_TICKET", cursor.getString(COLUMN_DAY_TICKET_INDEX) );
                    return cursor.getString(COLUMN_DAY_TICKET_INDEX);
                }
            } while (cursor.moveToNext());
        }
        return "";
    }

    public String getSports (int correspondingCivicID) {
        Cursor cursor = database.query(DATABASE_TABLE, new String [] {KEY_CORRESPONDING_CIVIC_ID, KEY_SPORTS}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(COLUMN_ID_INDEX) == correspondingCivicID) {
                    Log.d("DAY_TICKET", cursor.getString(COLUMN_SPORTS_INDEX) );
                    return cursor.getString(COLUMN_DAY_TICKET_INDEX);
                }
            } while (cursor.moveToNext());
        }
        return "";
    }


    private class FurtherInformationDBOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " ("
                + KEY_CORRESPONDING_CIVIC_ID + " integer primary key, "
                + KEY_DAY_TICKET + " text, "
                + KEY_SPORTS + " text);";

        public FurtherInformationDBOpenHelper(Context c, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
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
