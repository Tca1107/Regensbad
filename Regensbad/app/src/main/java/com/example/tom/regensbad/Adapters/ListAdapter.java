package com.example.tom.regensbad.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.R;

import java.util.ArrayList;

/**
 * Created by Sebastian on 26.08.2015.
 */
public class ListAdapter extends ArrayAdapter<CivicPool> {

    private ArrayList<CivicPool> listItems;
    private Context context;

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
            RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingbar_averageRatingPreview);
            TextView distance = (TextView) v.findViewById(R.id.textview_distance);
            TextView openStatus = (TextView) v.findViewById(R.id.textview_openStatus);

            poolName.setText(pool.getName());
            poolType.setText(pool.getType());
            //Rating über parse.com
            distance.setText(getDistance());
            openStatus.setText(getOpenStatus());
        }
        return v;
    }

    private String getOpenStatus() {
        return "geöffnet";
    }

    private int getDistance() {
        return 0;
    }

}
