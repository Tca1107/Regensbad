package com.example.tom.regensbad.Activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.tom.regensbad.R;

public class SearchCivicPoolsActivity extends ActionBarActivity {

    private static final String KEY_FOR_INTENT_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        setContentView(R.layout.activity_search_civic_pools);
        readIntent();
    }

    private void initializeUIElements() {
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
    }

    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = SearchCivicPoolsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(SearchCivicPoolsActivity.this.getResources().getColor(R.color.blue_dark_primary_color));
    }

    private void readIntent() {
        Bundle extras = getIntent().getExtras();
        String query = extras.getString(KEY_FOR_INTENT_EXTRA);
        performTheSearch(query);
    }


    private void performTheSearch(String query) {
        Log.d("it worked", query);
    }
}
