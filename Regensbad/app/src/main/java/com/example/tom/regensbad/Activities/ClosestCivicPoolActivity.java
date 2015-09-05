package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ClosestCivicPoolActivity extends ActionBarActivity implements LocationUpdater.OnLocationUpdateReceivedListener,
        DistanceDataProvider.DistanceDataReceivedListener, View.OnClickListener {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    /* Properties for location updates */
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters


    /* Constants of the type String needed for the execution of the queries to the parse backend. */
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
    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_CORRESPONDING_CIVIC_ID = "correspondingCivicID";
    private static final String PARSE_USERNAME = "userName";
    private static final String PARSE_DATE = "date";
    private static final String PARSE_COMMENT = "comment";
    private static final String PARSE_RATING = "rating";
    private static final String PARSE_CREATED_AT = "createdAt";

    /* Constant of the type String needed for the creation of a drawable from a String. */
    private static final String DRAWABLE = "drawable";

    /* Separators */
    private static final int SUBSTRING_SEPARATOR_START = 0;
    private static final int SUBSTRING_SEPARATOR_END = 2;

    /* Numeric constants needed to calculate distances and to bring the in an acceptable form. */
    private static final int FLOAT_DISTANCE_LENGTH = 1;
    private static final int DISTANCE_LOCATION_IN_FLOAT = 0;
    private static final int KILOMETERS_FACTOR = 1000;
    private static final double DOUBLE_CUTTING_FACTOR = 100.0;
    private static final double CONTROL_DISTANCE = 100.0;

    /* Constants needed for the progress dialog/progress bar. */
    private static final String PROGRESS_BAR_MESSAGE = "Nähestes Bad wird ermittelt.";
    private static final int PROGRESS_BAR_MIN = 0;
    private static final int PROGRESS_BAR_MAX = 100;
    private static final int PROGRESS_BAR_SLEEP_TIME = 1000;

    /* Constants needed for making the pool images' design acceptable. */
    private static final int SCREEN_HEIGHT_DIVIDE_FACTOR = 6;
    private static final int SCREEN_HEIGHT_DIVIDE_FACTOR_EXTENDED = 12;
    private static final int SCREEN_MAX_HEIGHT = 1000;


    /* User interface elements */
    private ImageView poolPicture;
    private TextView textName;
    private TextView textOpenTime;
    private TextView textDistance;
    private RatingBar averageRating;
    private TextView textPhoneNumber;
    private TextView textWebsite;
    private Button showMapButton;
    private Button startNavigationButton;
    private RelativeLayout relativeLayout;
    private TextView usernameComment;
    private RatingBar ratingComment;
    private TextView comment;
    private TextView dateComment;
    private TextView latestComment;
    private Button makeACommnent;
    private Button allComments;


    /* Instance variables needed for the progress dialog/progress bar. */
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();


    /* Instance variables needed to calculate the user's distance to the pools and to request data needed for the intents. */
    private double userLat;
    private double userLong;
    private DistanceDataProvider distanceDataProvider;
    private ParseObject currentPool;
    private int closestPoolCivicID;
    private double poolLati;
    private double poolLongi;
    private String poolWebsite;
    private String poolPhoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        initializeActionBar();
        registerOnClickListeners();
        fetchUserLocation();
        fetchDataFromParse();
    }




    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
     with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .
     It enables the bck button and styles the action bar the way it is defined in the corresponding xml layout file.*/
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

    /* This method first checks whether the system is connected to the internet. If so, it initializes the progress dialog
    * and starts to fetch data from the parse backend. If the system is not connected to the internet, a respective dialog
    * is called that tells the user that he or she is not able to download data from the internet. */
    private void fetchDataFromParse() {
            if (checkIfConnectedToInternet() == true) {
                createProgressBar();
                processParseComQuery();
            } else {
                // GetLatestUpdateFromDatabase(); maybe rather sorry you are not connected to the internet
            }

    }


    /* This method retrieves the latest CommentRating Object from parse.com It was written using the parse.com documentation at:
   https://parse.com/docs/android/guide#objects-retrieving-objects
   https://parse.com/docs/android/guide#queries .*/
    private void getDataForLatestComment() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_CORRESPONDING_CIVIC_ID, closestPoolCivicID);
        // following line from http://stackoverflow.com/questions/27971733/how-to-get-latest-updated-parse-object-in-android
        query.orderByDescending(PARSE_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null) {
                    getCommentDataAndSetItOnScreen(list);
                }
            }
        });

    }

    /* This method retrieves CommentRating objects from the parse backend, which consist of the username, the comment itself,
    * the user's rating, and the date the comment was submitted. On top, it calculates which one of the commentRating objects
    * is the latest and displays it on the screen. */
    private void getCommentDataAndSetItOnScreen(List<ParseObject> list) {
        if (list.size() > 0) {
            String date = "";
            ParseObject currentObject = list.get(0);
            Log.d("Datum", String.valueOf(currentObject.getDate(PARSE_CREATED_AT)));
            usernameComment.setText(currentObject.getString(PARSE_USERNAME));
            comment.setText(currentObject.getString(PARSE_COMMENT));
            dateComment.setText(currentObject.getString(PARSE_DATE));
            ratingComment.setRating((int) currentObject.getNumber(PARSE_RATING));
            Log.d("RATING", String.valueOf(currentObject.getNumber(PARSE_RATING)));
            setRatingInDetailView(list);
        }
    }

    private void setRatingInDetailView(List<ParseObject> list) {
        int counter = 0;
        float aggregated = 0;
        for (int i = 0; i < list.size(); i++) {
            ParseObject currentObject = list.get(i);
            aggregated += (int)currentObject.getNumber(PARSE_RATING);
            counter++;
        }
        float rating = aggregated/counter;
        averageRating.setRating(rating);
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
                    getDataAndFindClosestCivicPool(list);
                }
            }
        });
    }

    /* Depending on the user's current position, this method calculates which one of is the closest. */
    private void getDataAndFindClosestCivicPool(List<ParseObject> list) {
        double controlDistance = CONTROL_DISTANCE;
        int poolIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            ParseObject civicPoolToCalculateDistanceWith = list.get(i);
            double latitude = (double)civicPoolToCalculateDistanceWith.getNumber(PARSE_LATI);
            double longitude = (double)civicPoolToCalculateDistanceWith.getNumber(PARSE_LONGI);
            double distance = calculateDistance(latitude, longitude);
            if (distance < controlDistance) {
                controlDistance = distance;
                poolIndex = i;
            }
        }
        ParseObject closestPool = list.get(poolIndex);
        setTheInstanceVariables(closestPool);
        setDataOnScreen(closestPool, controlDistance);
        getDataForLatestComment();
    }

    private void setTheInstanceVariables(ParseObject closestPool) {
        closestPoolCivicID = (int) closestPool.getNumber(PARSE_CIVIC_ID);
        poolLati = (double)closestPool.getNumber(PARSE_LATI);
        poolLongi = (double)closestPool.getNumber(PARSE_LONGI);
        poolPhoneNumber = closestPool.getString(PARSE_PHONE_NUMBER);
        poolWebsite = closestPool.getString(PARSE_WEBSITE);

    }

    /* Sets the data of the closest civic pool to the screen. */
    private void setDataOnScreen(ParseObject closestPool, double controlDistance) {
        currentPool = closestPool;
        String poolName = closestPool.getString(PARSE_NAME);
        calculateDistanceByCar(poolName);
        textName.setText(poolName);
        createTimeView(closestPool.getString(PARSE_OPEN_TIME), closestPool.getString(PARSE_CLOSE_TIME));
        textPhoneNumber.setText(closestPool.getString(PARSE_PHONE_NUMBER));
        setPoolPicture(closestPool.getString(PARSE_PIC_PATH));
    }

    /*Following four lines retrieved from: http://stackoverflow.com/questions/13105430/android-setting-image-from-string
    * Helps to get the image for the civic pool from a String. */
    private void setPoolPicture(String picPath) {
        setScreenHeight();
        int id = getResources().getIdentifier(picPath, DRAWABLE, getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        poolPicture.setImageDrawable(drawable);
        poolPicture.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    /* Created with the help of: http://stackoverflow.com/questions/22743153/android-device-screen-resolution
    * Calculates the screen height and sets the height of the relative layout containing the pool image correspondingly. */
    private void setScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        if (height >= SCREEN_MAX_HEIGHT) {
            relativeLayout.getLayoutParams().height = height/SCREEN_HEIGHT_DIVIDE_FACTOR;
        } else {
            relativeLayout.getLayoutParams().height = height /SCREEN_HEIGHT_DIVIDE_FACTOR_EXTENDED;
        }
    }

    /* This method calculates the "real" distance between the user's position and the closest pool.
    * This is to say, it tells the user how many kilometers he or she would need to drive by their car in order
    * to reach the site of the civic pool. This is done by executing an async task, downloading information from the
    * Google Maps API. At first, we tried to use the async task for each and every object in the AllCivicPoolsActivity, as well.
    * However, executing an async task more than once in a single running activity is unfortunately not possible, which is why we
    * had to resort to calculating beelines instead of actual driver's routes. Calculating the former is possible without
    * using the async task class and can therefore be performed faster and more than once at a time. */
    private void calculateDistanceByCar(String poolName) {
        distanceDataProvider = new DistanceDataProvider();
        distanceDataProvider.setOnDistanceDataReceivedListener(this);
        String destinationAddress = poolName.replace(" ", "-"); //From: http://stackoverflow.com/questions/6932163/removing-spaces-from-string
        String downloadString = "http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + destinationAddress + "&mode=driving&sensor=false";
        distanceDataProvider.execute(downloadString);
    }

    /* Brings the opening times in an acceptable form. */
    private void createTimeView(String openTime, String closeTime) {
        String timeString = " " + openTime.substring(SUBSTRING_SEPARATOR_START,SUBSTRING_SEPARATOR_END)
                + ":" + openTime.substring(SUBSTRING_SEPARATOR_END)
                + " - " + closeTime.substring(SUBSTRING_SEPARATOR_START, SUBSTRING_SEPARATOR_END)
                + ":" + closeTime.substring(SUBSTRING_SEPARATOR_END);
        textOpenTime.setText(timeString);

    }

    /* Look at Android developer documentation:
     * http://developer.android.com/reference/android/location/Location.html#distanceBetween(double, double, double, double, float[])*/
    private double calculateDistance(double poolLat, double poolLong) {
        float[]dist=new float[FLOAT_DISTANCE_LENGTH];
        Location.distanceBetween(userLat, userLong, poolLat, poolLong, dist);
        double toReturn = cutTheRedundantPlaces((double) dist[DISTANCE_LOCATION_IN_FLOAT] / KILOMETERS_FACTOR);
        return toReturn;
    }

    /* Created with the help of: http://stackoverflow.com/questions/11701399/round-up-to-2-decimal-places-in-java */
    private double cutTheRedundantPlaces(double longDistanceDouble) {
        return Math.round(longDistanceDouble * DOUBLE_CUTTING_FACTOR)/DOUBLE_CUTTING_FACTOR;
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


    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_detail_view);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);
        poolPicture = (ImageView)findViewById(R.id.imageView_bathIMG);
        textName = (TextView) findViewById(R.id.textView_bathName);
        textName.setText("");
        averageRating = (RatingBar)findViewById(R.id.ratingbar_detailAverageRating);
        textOpenTime = (TextView) findViewById(R.id.textview_openTime);
        textDistance = (TextView) findViewById(R.id.textView_detail_distance);
        textPhoneNumber = (TextView) findViewById(R.id.text_phoneNumber);
        textWebsite = (TextView) findViewById(R.id.text_website);
        showMapButton = (Button) findViewById(R.id.button_showOnMap);
        startNavigationButton = (Button) findViewById(R.id.button_nav);
        latestComment = (TextView)findViewById(R.id.text_view_latest_comment);
        usernameComment = (TextView)findViewById(R.id.text_view_username_comment);
        ratingComment = (RatingBar)findViewById(R.id.ratingbar_comment_rating);
        comment = (TextView)findViewById(R.id.text_view_comment);
        dateComment = (TextView)findViewById(R.id.text_view_comment_date);
        allComments = (Button)findViewById(R.id.button_show_all_comments);
        makeACommnent = (Button)findViewById(R.id.button_make_a_comment);

    }


    private void fetchUserLocation() {
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
        // if userLocation could not be fetched, the coordinates of the Regensburg Main Station are used.
    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = ClosestCivicPoolActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ClosestCivicPoolActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }

    private void registerOnClickListeners() {
        showMapButton.setOnClickListener(this);
        startNavigationButton.setOnClickListener(this);
        textWebsite.setOnClickListener(this);
        textPhoneNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_showOnMap:
                goToMap();
                break;
            case R.id.button_nav:
                goToNavigation();
                break;
            case R.id.text_website:
                startBrowerAndGoToWebsite();
                break;
            case R.id.text_phoneNumber:
                makeACall();
                break;

        }

    }

    private void makeACall() {
        //From: http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
        Intent makeCall = new Intent(Intent.ACTION_CALL);
        makeCall.setData(Uri.parse("tel:" + poolPhoneNumber));
        startActivity(makeCall);
    }

    private void startBrowerAndGoToWebsite() {
        //From: http://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
        Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(poolWebsite));
        startActivity(startBrowser);
    }

    private void goToNavigation() {
        //From: http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
        Intent startNavigationIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + poolLati + "," + poolLongi));
        startActivity(startNavigationIntent);
    }

    private void goToMap() {
        Intent goToMap = new Intent(ClosestCivicPoolActivity.this, MapsActivity.class);
        goToMap.putExtra("ID", closestPoolCivicID);
        goToMap.putExtra("origin", "detail");
        startActivity(goToMap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_closest_civic_pool, menu);
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

    private void updateProgressBarStatus(int value) {
        progressBarStatus += value;
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
    public void onDataDistanceDataReceived(double dist) {
        textDistance.setText(String.valueOf(dist));
        updateProgressBarStatus(PROGRESS_BAR_MAX);
    }



}
