package com.example.tom.regensbad.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Persistence.RatingDatabase;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Sebastian on 26.08.2015.
 */
public class ListAdapter extends ArrayAdapter<CivicPool> {


    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String TIME_ZONE = "CET"; // Central European Time
    private static final int SUBSTRING_START = 11;
    private static final int SUBSTRING_END = 16;

    private static final String GREEN = "#4CAF50";
    private static final String RED = "#F44336";
    private static final String OPEN = "geöffnet";
    private static final String CLOSED = "geschlossen";

    /* Weekdays */
    private static final int MONDAY = 2;
    private static final int FRIDAY = 6;
    private static final int SATURDAY = 7;
    private static final int SUNDAY = 1;

    private ArrayList<CivicPool> listItems;
    private Context context;

    private TextView distance;
    private TextView openStatus;
    private RatingBar ratingBar;



    public ListAdapter (Context context, ArrayList<CivicPool> listItems){
        super(context, R.layout.single_lake_list_item, listItems);
        this.context = context;
        this.listItems = listItems;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_lake_list_item, null);
        }

        CivicPool pool = listItems.get(position);

        if(pool != null){
            TextView poolName = (TextView) v.findViewById(R.id.textview_lakeName);
            TextView poolType = (TextView) v.findViewById(R.id.textview_bathType);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingbar_averageRatingPreview);
            distance = (TextView) v.findViewById(R.id.textview_distance);
            openStatus = (TextView) v.findViewById(R.id.textview_openStatus);

            poolName.setText(pool.getName());
            poolType.setText(pool.getType());
            distance.setText(String.valueOf(pool.getCurrentDistance()));
            ratingBar.setRating(pool.getCurrentRating());


            String currentTime = fetchCurrentTime();
            if (getOpenStatus(Integer.valueOf(currentTime), pool)) {
                openStatus.setText(OPEN);
                openStatus.setTextColor(Color.parseColor(GREEN));
            } else {
                openStatus.setText(CLOSED);
                openStatus.setTextColor(Color.parseColor(RED));
            }
        }
        return v;
    }


    /* Method written with the help of: http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
  http://stackoverflow.com/questions/8077530/android-get-current-timestamp and
  http://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java .*/
    private int getWeekDayInfo() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return weekday;
    }

    private boolean getOpenStatus(int currentTime, CivicPool pool) {
      int weekday = getWeekDayInfo();
        if (MONDAY <= weekday && weekday <= FRIDAY) {
            int openTime = Integer.valueOf(pool.getOpenTime());
            int closeTime = Integer.valueOf(pool.getCloseTime());
            if (openTime <= currentTime && currentTime <= closeTime) {
                return true;
            } else {
                return false;
            }
        } else if (weekday == SATURDAY) {
            int openTime = Integer.valueOf(pool.getOpenTimeSat());
            int closeTime = Integer.valueOf(pool.getCloseTimeSat());
            if (openTime <= currentTime && currentTime <= closeTime) {
                return true;
            } else {
                return false;
            }
        } else {
            int openTime = Integer.valueOf(pool.getOpenTimeSun());
            int closeTime = Integer.valueOf(pool.getCloseTimeSun());
            if (openTime <= currentTime && currentTime <= closeTime) {
                return true;
            } else {
                return false;
            }
        }



    }

    /* This method was written using the resource http://stackoverflow.com/questions/8077530/android-get-current-timestamp
    * as a guideline. It gets the current time of the system. */
    private String fetchCurrentTime () {
        long time = System.currentTimeMillis();
        String formattedTime = formatTimeString(time);
        String formattedTimeOnlyHoursAndMinutes = formattedTime.substring(SUBSTRING_START, SUBSTRING_END);
        return formattedTimeOnlyHoursAndMinutes.substring(0,2) + formattedTimeOnlyHoursAndMinutes.substring(3);
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


}
