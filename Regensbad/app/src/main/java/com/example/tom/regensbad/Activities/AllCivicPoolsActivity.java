package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
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
import android.widget.ProgressBar;
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
        LocationUpdater.OnLocationUpdateReceivedListener{


    private ListView list;
    private ListAdapter adapter;
    private ArrayList<CivicPool> pools = new ArrayList<CivicPool>();

    private Database db;


    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();


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

    private static final String PROGRESS_BAR_MESSAGE = "Bäder werden heruntergeladen.";
    private static final int PROGRESS_BAR_MIN = 0;
    private static final int PROGRESS_BAR_MAX = 100;
    private static final int PROGRESS_BAR_SLEEP_TIME = 1000;


    private static final int FLOAT_DISTANCE_LENGTH = 1;
    private static final int DISTANCE_LOCATION_IN_FLOAT = 0;
    private static final int KILOMETERS_FACTOR = 1000;
    private static final double DOUBLE_CUTTING_FACTOR = 100.0;
    private static final int NUMBER_OF_POOLS_ON_SCREEN = 4;

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        initializeAdapter();
        initializePoolList();
        initializeDB();
        fetchUserLocation();
        fetchDataFromParse();
        initializeActionBar();
        handleClick();
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
            createProgressBar();
            db.deleteAllPoolItems();
            processParseComQuery();
        } else {
            GetLatestUpdateFromDatabase();
        }

    }

    /* This method was written using the tutorial which is available at:
    http://examples.javacodegeeks.com/android/core/ui/progressbar/android-progress-bar-example/ */

    private void createProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage(PROGRESS_BAR_MESSAGE);
        progressBar.setProgress(PROGRESS_BAR_MIN);
        progressBar.setMax(PROGRESS_BAR_MAX);
        progressBar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressBarStatus < PROGRESS_BAR_MAX){
                    progressBarStatus = getProgressBarStatus();
                    Log.d("PROGRESS", String.valueOf(progressBarStatus));
                    try {
                        Thread.sleep(PROGRESS_BAR_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBarHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });}
                Log.d("PROGRESS", String.valueOf(progressBarStatus));
                    if (progressBarStatus >= PROGRESS_BAR_MAX) {
                       /* try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } */
                        progressBar.dismiss();
                    }

                }
        }).start();

    }

    private int getProgressBarStatus() {
        return progressBarStatus;
    }

    private void GetLatestUpdateFromDatabase() {
        ArrayList<CivicPool> myPoolArray = new ArrayList<CivicPool>();
        myPoolArray.addAll(db.getAllPoolItems());
        ArrayList<CivicPool> myPoolArrayWithCorrectDistances = setTheDecimalPlacesOfCurrentDistanceRight(myPoolArray);
        Collections.sort(myPoolArrayWithCorrectDistances);
        adapter = new ListAdapter(this, myPoolArrayWithCorrectDistances);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<CivicPool> setTheDecimalPlacesOfCurrentDistanceRight(ArrayList<CivicPool> myPoolArray) {
        ArrayList<CivicPool> arrayToReturn = new ArrayList<CivicPool>();
        for (int i = 0; i < myPoolArray.size(); i++) {
            CivicPool pool = myPoolArray.get(i);
            // myPoolArray.remove(i);
            pool.setDecimalPlacesInCurrentDistance(cutTheRedundantPlaces(pool.getCurrentDistance()));
            arrayToReturn.add(pool);
        }
        return arrayToReturn;
    }



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
                    initializeAdapter();
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
            if (i == NUMBER_OF_POOLS_ON_SCREEN){
                updateProgressBarStatus(PROGRESS_BAR_MAX);
            }
        }
        // sort the pools by their distance to the current location of the user
        Collections.sort(pools);
    }

    private void updateProgressBarStatus(int addValue) {
        progressBarStatus += addValue;
    }


    /* Look at Android developer documentation:
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

    private void initializePoolList() {
        pools.clear();
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
    }

    private void initializeAdapter() {
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



    /*

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





       /*private void updateList() {
        pools.clear();
        adapter.notifyDataSetChanged();
    }



    */







}
