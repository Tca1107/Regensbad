package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.R;


public class CivicPoolDetailActivity extends ActionBarActivity implements DistanceDataProvider.DistanceDataReceivedListener, LocationUpdater.OnLocationUpdateReceivedListener {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";


    // Properties for location updates
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters

    private int ID;
    private double distance;
    private double userLat;
    private double userLong;


    private ImageView poolPicture;
    private TextView textName;
    private TextView textOpenTime;
    private TextView textDistance;
    private TextView textPhoneNumber;
    private TextView textWebsite;
    private Button showMapButton;
    private Button startNavigationButton;

    private DistanceDataProvider distanceDataProvider;
    private Database db;
    private CivicPool pool;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDatabase();
        initializeActionBar();
        getExtras();
        getDistance();
        initializeUIElements();
        handleInput();

    }

    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
    with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .*/
    private void initializeActionBar() {
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.home_screen_action_bar, null);
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        ((TextView)view.findViewById(R.id.text_view_action_bar_home_screen)).setTypeface(typeface);
        this.getSupportActionBar().setCustomView(view);
    }



    private void getDistance() {
        System.out.println("Es geht was!");
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();



        distanceDataProvider = new DistanceDataProvider();
        distanceDataProvider.setOnDistanceDataReceivedListener(this);
        //distanceCalculator = new DistanceCalculator(this);
        // hier dann ne for - schleife mit allen Pools
        // double lat = distanceCalculator.getLatitude();
        // double longi = distanceCalculator.getLongitude();
        // hier pool Namen aus Parse holen, unten Guggenberger See als Dummy


        String destinationAddress = pool.getName().replace(" ", "-"); //From: http://stackoverflow.com/questions/6932163/removing-spaces-from-string
        Log.d("Address: ", destinationAddress);
        Log.d("UserLat um 19:18", String.valueOf(userLat));
        String downloadString = "http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + destinationAddress + "&mode=driving&sensor=false";
        Log.d("wehdewh", downloadString);
        distanceDataProvider.execute(downloadString);
        //distanceDataProvider.execute("http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + "UniRegensburg" + "&mode=driving&sensor=false");

        // wir muessten das hier dann mit ner arraylist loesen, in die alle distances eingefuegt werden, dann wird der adapter informiert
        // per notifydatasetcahnged.
    }


    private void initializeDatabase() {
        db = new Database(this);
        db.open();
    }

    private void handleInput() {
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent(CivicPoolDetailActivity.this, MapsActivity.class);
                goToMap.putExtra("ID", pool.getID());
                goToMap.putExtra("origin", "detail");
                startActivity(goToMap);
            }
        });

        startNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From: http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
                Intent startNavigationIntent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + pool.getLati() + "," + pool.getLongi()));
                startActivity(startNavigationIntent);
            }
        });

        textWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From: http://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
                Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(pool.getWebsite()));
                startActivity(startBrowser);
            }
        });

        textPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From: http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
                Intent makeCall = new Intent(Intent.ACTION_CALL);
                makeCall.setData(Uri.parse("tel:" + pool.getPhoneNumber()));
                startActivity(makeCall);
            }
        });
    }

    private void getExtras() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        ID = extras.getInt("ID");
        pool = db.getPoolItem(ID);
    }

    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_detail_view);
        getTheElements();
        setTheContentOfTheElements();
        setPoolPictureFromPathString();
        createTimeView();

    }

    private void setTheContentOfTheElements() {
        textName.setText(pool.getName());
        textDistance.setText(String.valueOf(distance));
        // Log.d("INIDISTANCE: ", String.valueOf(distance));
        textPhoneNumber.setText(pool.getPhoneNumber());
    }

    private void getTheElements() {
        poolPicture = (ImageView)findViewById(R.id.imageView_bathIMG);
        textName = (TextView) findViewById(R.id.textView_bathName);
        textOpenTime = (TextView) findViewById(R.id.textview_openTime);
        textDistance = (TextView) findViewById(R.id.textView_detail_distance);
        textPhoneNumber = (TextView) findViewById(R.id.text_phoneNumber);
        textWebsite = (TextView) findViewById(R.id.text_website);
        showMapButton = (Button) findViewById(R.id.button_showOnMap);
        startNavigationButton = (Button) findViewById(R.id.button_nav);
    }

    /* Following four lines were created with the help of the following web resource:
    http://stackoverflow.com/questions/13105430/android-setting-image-from-string */
    private void setPoolPictureFromPathString() {
        String picPath = db.getPicPath(ID);
        int id = getResources().getIdentifier(picPath, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        poolPicture.setImageDrawable(drawable);
        poolPicture.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    private void createTimeView() {
        String timeString = " " + pool.getOpenTime().substring(0,2) + ":" + pool.getOpenTime().substring(2) + " - " + pool.getCloseTime().substring(0, 2) + ":" + pool.getCloseTime().substring(2);
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
        if (id == android.R.id.home){
        finish();}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataDistanceDataReceived(double dist) {
        distance = dist;
        textDistance.setText(String.valueOf(distance));
    }

    @Override
    public void onFormattedLocationReceived(String formattedLocation) {
        int separator = 0;
        for (int i = 0; i < formattedLocation.length(); i++) {
            char charToCheck = formattedLocation.charAt(i);
            if (charToCheck == ','){
                separator = i;
            }
        }
        String latString = formattedLocation.substring(0, separator);
        String longString = formattedLocation.substring(separator + 1);
        userLat = Double.parseDouble(latString);
        userLong = Double.parseDouble(longString);
    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }


}
