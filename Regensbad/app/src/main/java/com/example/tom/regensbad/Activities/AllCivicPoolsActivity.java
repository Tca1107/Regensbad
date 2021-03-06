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
import android.widget.TextView;

import com.example.tom.regensbad.Adapters.ListAdapter;
import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class AllCivicPoolsActivity extends ActionBarActivity implements
        LocationUpdater.OnLocationUpdateReceivedListener{

    private ListView list;
    private ListAdapter adapter;
    private ArrayList<CivicPool> pools = new ArrayList<CivicPool>();

    /* Databases as instance variables, needed for the ListView showing all civic pool items. */
    private Database db;

    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    private double userLat;
    private double userLong;
    private float ratingToReturn;


    // Properties for location updates
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters


    // String constants that are needed for the data retrieval from the backend
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
    private static final String PARSE_CURRENT_RATING = "currentRating";
    private static final String PARSE_OPEN_TIME_SAT = "openTimeSat";
    private static final String PARSE_CLOSE_TIME_SAT = "closeTimeSat";
    private static final String PARSE_OPEN_TIME_SUN = "openTimeSun";
    private static final String PARSE_CLOSE_TIME_SUN = "closeTimeSun";
    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_CORRESPONDING_CIVIC_ID = "correspondingCivicID";

    // prpgress bar constants
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



    /* Initializes the LocationUpdater and sets a listener on this activity. Moreover, it asks for location updates.*/
    private void fetchUserLocation() {
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
        // if userLocation could not be fetched, the coordinates of the Regensburg Main Station are used.
    }



    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
    with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .
     It sets up the action bar and loads the respective xml file with the help of a layout inflater.*/
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


    /* If the system is connected to the internet all the data needed for the civicpool objects is loaded from the backend.
    * Moreover, a progress bar is initialized and the database containing the information for the civicpools is deleted.
    * If the system is not connected to the internet, the needed data is retrieved from the database. */
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
    http://examples.javacodegeeks.com/android/core/ui/progressbar/android-progress-bar-example/
    It sets up a progress bar that closes when the download of the data for the civicpool objects is done. */
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
                    if (progressBarStatus >= PROGRESS_BAR_MAX) {
                        progressBar.dismiss();
                        progressBarStatus = 0;
                    }

                }
        }).start();

    }

    private int getProgressBarStatus() {
        return progressBarStatus;
    }

    /* Retrieves the data for the civic pools from the respective database. */
    private void GetLatestUpdateFromDatabase() {
        progressBarStatus = PROGRESS_BAR_MAX;
        ArrayList<CivicPool> myPoolArray = new ArrayList<CivicPool>();
        myPoolArray.addAll(db.getAllPoolItems());
        ArrayList<CivicPool> myPoolArrayWithCorrectDistances = setTheDecimalPlacesOfCurrentDistanceRight(myPoolArray);
        pools.clear();
        pools = myPoolArrayWithCorrectDistances;
        Collections.sort(myPoolArrayWithCorrectDistances);
        adapter = new ListAdapter(this, myPoolArrayWithCorrectDistances);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /* Cuts off the redundant places of the distance from the user to the civicpool, which is represented by a double.
    * The result is a number with two digits after the comma. */
    private ArrayList<CivicPool> setTheDecimalPlacesOfCurrentDistanceRight(ArrayList<CivicPool> myPoolArray) {
        ArrayList<CivicPool> arrayToReturn = new ArrayList<CivicPool>();
        for (int i = 0; i < myPoolArray.size(); i++) {
            CivicPool pool = myPoolArray.get(i);
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
                    pools.clear();
                    assignParseDataToArrayList(list);
                    initializeAdapter();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /* Using respective calls, this method gets all the data from needed to create civic pool objects that are put in
     * the array list that the adapter works with. Also, every civicpool object is put into the respective database after
      * it has been downloaded. */
    private void assignParseDataToArrayList(List<ParseObject> list) {
        for (int i = 0; i < list.size(); i++) {
            ParseObject civicPoolToAdd = list.get(i);
            String name = civicPoolToAdd.getString(PARSE_NAME);
            String type = civicPoolToAdd.getString(PARSE_TYPE);
            double latitude = (double) civicPoolToAdd.getNumber(PARSE_LATI);
            double longitude = (double) civicPoolToAdd.getNumber(PARSE_LONGI);
            String phoneNumber = civicPoolToAdd.getString(PARSE_PHONE_NUMBER);
            String website = civicPoolToAdd.getString(PARSE_WEBSITE);
            String openTime = civicPoolToAdd.getString(PARSE_OPEN_TIME);
            String closeTime = civicPoolToAdd.getString(PARSE_CLOSE_TIME);
            String picPath = civicPoolToAdd.getString(PARSE_PIC_PATH);
            int civicID = (int) civicPoolToAdd.getNumber(PARSE_CIVIC_ID);
            double currentDistance = calculateCurrentDistance(latitude, longitude);
            float currentRating = (int) civicPoolToAdd.getNumber(PARSE_CURRENT_RATING);
            String openTimeSat = civicPoolToAdd.getString(PARSE_OPEN_TIME_SAT);
            String closeTimeSat = civicPoolToAdd.getString(PARSE_CLOSE_TIME_SAT);
            String openTimeSun = civicPoolToAdd.getString(PARSE_OPEN_TIME_SUN);
            String closeTimeSun = civicPoolToAdd.getString(PARSE_CLOSE_TIME_SUN);
            CivicPool civicPoolFromParse = new CivicPool(name, type, latitude, longitude, phoneNumber, website, openTime,
                    closeTime, picPath, civicID, currentDistance, (float)currentRating, openTimeSat, closeTimeSat, openTimeSun, closeTimeSun);
            pools.add(civicPoolFromParse);
            db.addCivicPoolItem(civicPoolFromParse);
            if (i == NUMBER_OF_POOLS_ON_SCREEN) {
                updateProgressBarStatus(PROGRESS_BAR_MAX);
            }
            // sort the pools by their distance to the current location of the user
            Collections.sort(pools);
        }
    }


    /* Updates the status of the progressbar*/
    private void updateProgressBarStatus(int addValue) {
        progressBarStatus += addValue;
    }


    /* This method was created with the help of the Android developer documentation:
    * http://developer.android.com/reference/android/location/Location.html#distanceBetween(double, double, double, double, float[])
    * It calculates the distance between the user's location and the respective civic pool (by airline).*/
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

    /* Handles the clicks on an item in the list view. Starts the CivicPoolDetailActivity. */
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
            getMenuInflater().inflate(R.menu.menu_all_civic_pools_user_online, menu);
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
            case R.id.button_all_civic_pools_on_map:
                showAllCivicPoolsOnMap();
                return true;
            case R.id.sort_rating:
                sortRating();
                return true;
            case R.id.sort_distance:
                sortDistance();
                return true;
            case R.id.sort_type:
                sortType();
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

    private void showAllCivicPoolsOnMap() {
        Intent startMapWithAllPools = new Intent(AllCivicPoolsActivity.this, MapsActivity.class);
        startMapWithAllPools.putExtra("origin", "all");
        startActivity(startMapWithAllPools);
    }


    private void sortRating() {
        Collections.sort(pools, new Comparator<CivicPool>() {
            @Override
            public int compare(CivicPool arg0, CivicPool arg1) {
                return (Float.toString(arg1.getCurrentRating()).compareToIgnoreCase(Float.toString(arg0.getCurrentRating())));
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void sortDistance() {
        Collections.sort(pools);
        adapter.notifyDataSetChanged();
    }

    private void sortType() {
        Collections.sort(pools, new Comparator<CivicPool>() {
            @Override
            public int compare(CivicPool arg0, CivicPool arg1) {
                return arg0.getType().compareToIgnoreCase(arg1.getType());
            }
        });
        adapter.notifyDataSetChanged();
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

}
