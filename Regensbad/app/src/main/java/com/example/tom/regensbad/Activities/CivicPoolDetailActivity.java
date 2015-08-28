package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.tom.regensbad.R;


public class CivicPoolDetailActivity extends ActionBarActivity {

    private String name;
    private String type;
    private double lati;
    private double longi;
    private String phoneNumber;
    private String website;
    private String openTime;
    private String closeTime;
    private String picPath;

    TextView textName;
    TextView textOpenTime;
    TextView textPhoneNumber;
    TextView textWebsite;

    Button showMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        initializeUIElements();
        handleInput();

    }

    private void handleInput() {
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent(CivicPoolDetailActivity.this, MapsActivity.class);
                goToMap.putExtra("name", name);
                goToMap.putExtra("lati", lati);
                goToMap.putExtra("longi", longi);
                goToMap.putExtra("origin", "detail");
                startActivity(goToMap);
            }
        });
    }

    private void getExtras() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        name = extras.getString("name");
        type = extras.getString("type");
        lati = extras.getDouble("lati");
        longi = extras.getDouble("longi");
        phoneNumber = extras.getString("number");
        website = extras.getString("website");
        openTime = extras.getString("openTime");
        closeTime = extras.getString("closeTime");
        picPath = extras.getString("imgPath");

    }

    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_detail_view);

        textName = (TextView) findViewById(R.id.textView_bathName);
        textOpenTime = (TextView) findViewById(R.id.textview_openTime);
        textPhoneNumber = (TextView) findViewById(R.id.text_phoneNumber);
        textWebsite = (TextView) findViewById(R.id.text_website);

        textName.setText(name);
        System.out.println("" + name);
        textPhoneNumber.setText(phoneNumber);
        textWebsite.setText(website);

        createTimeView();

        showMapButton = (Button) findViewById(R.id.button_showOnMap);
    }

    private void createTimeView() {
        String timeString = " " + openTime.substring(0,2) + ":" + openTime.substring(2) + " - " + closeTime.substring(0,2) + ":" + closeTime.substring(2);
        textOpenTime.setText(timeString);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = CivicPoolDetailActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(CivicPoolDetailActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_civic_pool_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
