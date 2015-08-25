package com.example.tom.regensbad.Activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tom.regensbad.R;

public class SearchCivicPoolsActivity extends ActionBarActivity {

    private static final String KEY_FOR_INTENT_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_civic_pools);
        readIntent();
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
