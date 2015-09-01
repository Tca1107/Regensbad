package com.example.tom.regensbad.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tom.regensbad.Activities.AllCivicPoolsActivity;
import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.LocationService.DistanceCalculator;
import com.example.tom.regensbad.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sebastian on 26.08.2015.
 */
public class ListAdapter extends ArrayAdapter<CivicPool> {


    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String TIME_ZONE = "CET"; // Central European Time
    private static final int SUBSTRING_START = 11;
    private static final int SUBSTRING_END = 16;

    private ArrayList<CivicPool> listItems;
    private Context context;
    private CivicPool pool;

    private TextView distance;
    private TextView openStatus;

    private static final String GREEN = "#4CAF50";
    private static final String RED = "#F44336";
    private static final String OPEN = "geöffnet";
    private static final String CLOSED = "geschlossen";

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

        pool = listItems.get(position);

        if(pool != null){
            TextView poolName = (TextView) v.findViewById(R.id.textview_lakeName);
            TextView poolType = (TextView) v.findViewById(R.id.textview_bathType);
            RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingbar_averageRatingPreview);
            distance = (TextView) v.findViewById(R.id.textview_distance);
            openStatus = (TextView) v.findViewById(R.id.textview_openStatus);

            poolName.setText(pool.getName());
            poolType.setText(pool.getType());
            distance.setText(String.valueOf(pool.getCurrentDistance()));

            //Rating über parse.com

            //getDistance();
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

  private boolean getOpenStatus(int currentTime, CivicPool pool) {
      int openTime = Integer.valueOf(pool.getOpenTime());
      int closeTime = Integer.valueOf(pool.getCloseTime());
      if (openTime <= currentTime && currentTime <= closeTime) {
          return true;
      } else {
          return false;
      }



        //From: http://stackoverflow.com/questions/5369682/get-current-time-and-date-on-android
        /* Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        Log.d("HOURS", String.valueOf(hours));

        int openHours;
        if(pool.getOpenTime().substring(0).equals("0")){
            openHours = Integer.parseInt(pool.getOpenTime().substring(1, 2));
        }else{
            openHours = Integer.parseInt(pool.getOpenTime().substring(0, 2));

        }

        if(openHours <= hours && Integer.parseInt(pool.getCloseTime().substring(0, 2)) >= hours){
            openStatus.setText(R.string.string_open);
            openStatus.setTextColor(Color.parseColor(green)); //Found no other solution, getResourses doesn´t work in adapter classes
        }else{
            openStatus.setText(R.string.string_closed);
            openStatus.setTextColor(Color.parseColor(red));
        } */

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

    /*
    private void getDistance() {
        DistanceCalculator calculator = new DistanceCalculator(context);
        System.out.println("Adapter: "+pool.getID());
        distance.setText(String.valueOf(calculator.calculateDistanceToPool(pool.getID())));
    } */

}
