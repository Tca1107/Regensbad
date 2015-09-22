package com.example.tom.regensbad.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tom.regensbad.R;

public class NoDataAvailableActivity extends ActionBarActivity {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        initializeActionBar();
    }

    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
   with Android", which is available at:
    http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .*/
    private void initializeActionBar() {
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.home_screen_action_bar, null);
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        ((TextView)view.findViewById(R.id.text_view_action_bar_home_screen)).setTypeface(typeface);
        this.getSupportActionBar().setCustomView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_no_data_available, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            goBackToHomeScreen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Created with the help of: https://parse.com/docs/android/guide#users
    private void goBackToHomeScreen() {
        Intent goBackToHomeScreen = new Intent (NoDataAvailableActivity.this, HomeScreenActivity.class);
        // From: http://stackoverflow.com/questions/6397576/finish-to-go-back-two-activities
        goBackToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackToHomeScreen);
    }
}
