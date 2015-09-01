package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tom.regensbad.Adapters.ListAdapter;
import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.LocationService.DistanceCalculator;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class AllCivicPoolsActivity extends ActionBarActivity implements
        DistanceDataProvider.DistanceDataReceivedListener, LocationUpdater.OnLocationUpdateReceivedListener{


    // Distance Data Provider dann in der DetailbActivity, um den genauen Wert zu bekommen (der Distanz), hier wird ja sonst mit Luftlinie gerrechnet

    private ListView list;
    private ListAdapter adapter;
    private ArrayList<CivicPool> pools = new ArrayList<CivicPool>();

    private Database db;
    private DistanceDataProvider distanceDataProvider;
    private DistanceCalculator distanceCalculator;

    private double userLat;
    private double userLong;


    // Properties for location updates
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters

    private static final String PARSE_CIVIC_POOL = "CivicPool";
    private static final String PARSE_NAME = "name";
    private static final String PARSE_TYPE = "type";
    private static final String PARSE_LATI = "lati";
    private static final String PARSE_LONGI = "longi";
    private static final String PARSE_PHONE_NUMBER = "phoneNumber";
    private static final String PARSE_WEBSITE = "website";
    private static final String PARSE_OPEN_TIME = "openTime";
    private static final String PARSE_CLOSE_TIME = "closeTime";
    private static final String PARSE_PIC_PATH = "picPath";
    private static final String PARSE_CIVIC_ID = "civicID";


    private static final int FLOAT_DISTANCE_LENGTH = 1;
    private static final int DISTANCE_LOCATION_IN_FLOAT = 0;
    private static final int KILOMETERS_FACTOR = 1000;
    private static final double DOUBLE_CUTTING_FACTOR = 100.0;

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        initPoolList();
        initializeDB();
        fetchUserLocation();
        fetchDataFromParse();
        initializeActionBar();
        handleClick();
        // getDistances();

    }

    private void fetchUserLocation() {
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
        // if userLocation could not be fetched, the coordinates of the Regensburg Main Station are used.
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


    private void fetchDataFromParse() {
        if (checkIfConnectedToInternet() == true) {
            db.deleteAllPoolItems();
            processParseComQuery();
            //CleanTheDatabaseAndSaveThePoolsIntoTheDatabase();
        } else {
            GetLatestUpdateFromDatabase();
        }

    }

    private void GetLatestUpdateFromDatabase() {
        pools.clear();
        pools.addAll(db.getAllPoolItems());
        Log.d("allepools", String.valueOf(pools));
        adapter.notifyDataSetChanged();
    }

    /*
    private void CleanTheDatabaseAndSaveThePoolsIntoTheDatabase() {
        db.deleteAllPoolItems();
        for (int i = 0; i < pools.size(); i++) {
            CivicPool poolToAdd = pools.get(i);
            db.addCivicPoolItem(poolToAdd);
        }
    } */


    /* This method retrieves the civic pool objects from the parse.com backend and adds them
    to the array list, that the adapter is set on. It was written using the parse.com documentation at:
    https://parse.com/docs/android/guide#objects-retrieving-objects
     https://parse.com/docs/android/guide#queries .*/
    private void processParseComQuery () {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CIVIC_POOL);
        query.whereExists(PARSE_NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    Log.d("Seen RETRIEVED", String.valueOf(list.size()) + " Seen");
                    pools.clear();
                    assignParseDataToArrayList(list);
                    initAdapter();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void assignParseDataToArrayList(List<ParseObject> list) {
        for (int i = 0; i < list.size(); i++) {
            ParseObject civicPoolToAdd = list.get(i);
            String name = civicPoolToAdd.getString(PARSE_NAME);
            String type = civicPoolToAdd.getString(PARSE_TYPE);
            double latitude = (double)civicPoolToAdd.getNumber(PARSE_LATI);
            double longitude = (double)civicPoolToAdd.getNumber(PARSE_LONGI);
            String phoneNumber = civicPoolToAdd.getString(PARSE_PHONE_NUMBER);
            String website = civicPoolToAdd.getString(PARSE_WEBSITE);
            String openTime = civicPoolToAdd.getString(PARSE_OPEN_TIME);
            String closeTime = civicPoolToAdd.getString(PARSE_CLOSE_TIME);
            String picPath = civicPoolToAdd.getString(PARSE_PIC_PATH);
            int civicID = (int)civicPoolToAdd.getNumber(PARSE_CIVIC_ID);
            double currentDistance = calculateCurrentDistance(latitude, longitude);
            CivicPool civicPoolFromParse = new CivicPool(name, type, latitude, longitude, phoneNumber, website, openTime, closeTime, picPath, civicID, currentDistance);
            pools.add(civicPoolFromParse);
            db.addCivicPoolItem(civicPoolFromParse);
        }
        // sort the pools by their distance to the current location of the user
        Collections.sort(pools);
        Log.d("ALLE AUS DB", String.valueOf(db.getAllPoolItems()));
    }


    /* Did we use a source for that? Maybe refer to the android developer documentation
    * http://developer.android.com/reference/android/location/Location.html#distanceBetween(double, double, double, double, float[])*/
    private double calculateCurrentDistance(double poolLat, double poolLong) {
        float[]dist=new float[FLOAT_DISTANCE_LENGTH];
        Location.distanceBetween(userLat, userLong, poolLat, poolLong, dist);
        double toReturn = cutTheRedundantPlaces((double) dist[DISTANCE_LOCATION_IN_FLOAT]/KILOMETERS_FACTOR);
        return toReturn;
    }

    /* Created with the help of: http://stackoverflow.com/questions/11701399/round-up-to-2-decimal-places-in-java */
    private double cutTheRedundantPlaces(double longDistanceDouble) {
        return Math.round(longDistanceDouble * DOUBLE_CUTTING_FACTOR)/DOUBLE_CUTTING_FACTOR;
    }

    private void getDistances() {
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

            String destinationAddress = "GuggenbergerSee";
            Log.d("UserLat um 19:18", String.valueOf(userLat));
            String downloadString = "http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + destinationAddress + "&mode=driving&sensor=false";
            distanceDataProvider.execute(downloadString);
            //distanceDataProvider.execute("http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + "UniRegensburg" + "&mode=driving&sensor=false");

        // wir muessten das hier dann mit ner arraylist loesen, in die alle distances eingefuegt werden, dann wird der adapter informiert
        // per notifydatasetcahnged.
    }

    private void handleClick() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CivicPool pool = (CivicPool) adapterView.getItemAtPosition(i);

                Intent showDetailView = new Intent(AllCivicPoolsActivity.this, CivicPoolDetailActivity.class);
                showDetailView.putExtra("ID", pool.getID());

                startActivity(showDetailView);
            }
        });
    }

    private void initPoolList() {
        updateList();
    }

    private void updateList() {
        pools.clear();
        //pools.addAll(db.getAllPoolItems());
        adapter.notifyDataSetChanged();
    }



    private void initializeDB() {
        db = new Database(this);
        db.open();
    }


    //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
    private void initializeUIElements() {
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_list_view);
        list = (ListView) findViewById(R.id.listview_lake_list);
        initAdapter();
    }

    private void initAdapter() {
        adapter = new ListAdapter(this, pools);
        list.setAdapter(adapter);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = AllCivicPoolsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(AllCivicPoolsActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_all_civic_pools, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_myAccount:
                changeToMyAccountActivity();
                return true;
            case R.id.action_logout:
                goBackToHomeScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // from https://parse.com/docs/android/guide#users
    private void goBackToHomeScreen() {
        ParseUser.logOut();
        finish();
    }

    private void changeToMyAccountActivity() {
        Intent changeToMyAccountActivity = new Intent (AllCivicPoolsActivity.this, MyAccountActivity.class);
        startActivity(changeToMyAccountActivity);
    }

    @Override
    public void onDataDistanceDataReceived(double dist) {
            Log.d("krieg mas", String.valueOf(dist));
       // die können wir nur in der DetailActivity verwenden!!! Async Task geht nur einmal! :-(

            CivicPool test = new CivicPool("Guggenberger See", "See", 48.977177, 12.223866, "09414009615", "http://www.landkreis-regensburg.de/Freizeit-Tourismus/Freizeitangebote/Baden/GuggenbergerSee(EU).aspx", "0800", "1900", "path", 11, 3.3);
            //CivicPool test2 = new CivicPool ("Seee in da Hood", "See", 56.2132, 21.3333, "09417777", "inetadresse", "0700", "1700", "path", 23);
            pools.clear();
            pools.add(test);
            Log.d("POOOOLS", String.valueOf(pools));
            adapter = new ListAdapter(this, pools);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
    }

    /* This method checks whether the system has access to the internet.
    * It was created taking the resource which can be found at the following link, as a guideline:
    * http://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android*/
    private boolean checkIfConnectedToInternet () {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

}
