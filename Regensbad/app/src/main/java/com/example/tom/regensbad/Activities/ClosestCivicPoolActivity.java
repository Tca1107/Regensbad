package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.regensbad.Domain.CommentRating;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.Persistence.FurtherInformationDatabase;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


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
    private static final String PARSE_CURRENT_RATING = "currentRating";
    private static final String PARSE_UP_VOTES = "upVotes";
    private static final String PARSE_OPEN_TIME_SAT = "openTimeSat";
    private static final String PARSE_CLOSE_TIME_SAT = "closeTimeSat";
    private static final String PARSE_OPEN_TIME_SUN = "openTimeSun";
    private static final String PARSE_CLOSE_TIME_SUN = "closeTimeSun";

    /* Constant of the type String needed for the creation of a drawable from a String. */
    private static final String DRAWABLE = "drawable";

    /* Separators */
    private static final int SUBSTRING_SEPARATOR_START = 0;
    private static final int SUBSTRING_SEPARATOR_END = 2;

    /* Weekdays */
    private static final int MONDAY = 2;
    private static final int FRIDAY = 6;
    private static final int SATURDAY = 7;
    private static final int SUNDAY = 1;

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

    private static final double DEFAULT_LAT = 49.010259;
    private static final double DEFAULT_LONG = 12.100722;

    /* Constants of the type String needed for the Toasts. */
    private static final String DEFAULT_LOCATION_TOAST = "Kein GPS-Empfang! Der Regensburger Hauptbahnhof wird als Standort angenommen.";
    private static final String NOT_ALLOWED_TO_COMMENT = "Sie haben keinen Account oder sind nicht eingeloggt. Sie können daher keine Kommentare oder Bewertungen abgeben.";
    private static final String YOU_ARE_OFFLINE = "Sie sind nicht mit dem Internet verbunden. Sie können daher keine Kommentare oder Bewertungen abgeben.";
    private static final int MIN_COMMENT_LENGTH = 5;
    private static final int MAX_COMMENT_LENGTH = 250;
    private static final String COMMENT_TOO_SHORT = "Ein Kommentar muss mindestens fünf Zeichen umfassen.";
    private static final String COMMENT_TOO_LONG = "Ein Kommentar darf nicht mehr als 250 Zeichen umfassen.";
    private static final String FORGOT_RATING = "Bitte geben Sie eine Bewertung ab.";
    private static final int MIN_RATING = 1;

    /* Constants used to calculate the current time. */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String TIME_ZONE = "CET"; // Central European Time
    private static final int SUBSTRING_TIME_START = 11;
    private static final int SUBSTRING_TIME_END = 16;
    private static final int SUBSTRING_DAY_START = 8;
    private static final int SUBSTRING_DAY_END = 10;
    private static final int SUBSTRING_MONTH_START = 5;
    private static final int SUBSTRING_MONTH_END = 7;
    private static final int SUBSTRING_YEAR_START = 0;
    private static final int SUBSTRING_YEAR_END = 4;

    private static final int ZERO_UP_VOTES = 0;

    private static final double LINE_SPACING = 1.2;


    private static final String OKAY = "OK";
    private static final String PIECES_OF_INFORMATION = "Weitere Informationen";
    private static final String DAY_TICKET_PRICE = "Tagesticket: ";
    private static final String NO_CONTENT_AVAILABLE = "Leider kein Inhalt verfügbar, da keine Verbindung zum Internet besteht.";

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
    private ImageButton furtherInformation;


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
    private int listsize = 0;
    private float sumOfRatings = 0;


    private FurtherInformationDatabase furtherInformationDatabase;
    private String dayTicket;
    private String infoOnCivicPool;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        initializeActionBar();
        initializeFurtherInformationDatabase();
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
                changeToNoDataAvailableActivity();
            }

    }

    private void changeToNoDataAvailableActivity() {
        Intent changeToNoDataAvailableActivity = new Intent (ClosestCivicPoolActivity.this, NoDataAvailableActivity.class);
        startActivity(changeToNoDataAvailableActivity);
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
        sumOfRatings = aggregated;
        averageRating.setRating((int) rating);
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
        setInstanceVariablesForFurtherInformation();
        initializeFurtherInformationDatabase();
    }

    private void setInstanceVariablesForFurtherInformation() {
        dayTicket = furtherInformationDatabase.getDayTicket(closestPoolCivicID);
        infoOnCivicPool = furtherInformationDatabase.getSports(closestPoolCivicID);
    }

    private void initializeFurtherInformationDatabase() {
        furtherInformationDatabase = new FurtherInformationDatabase(this);
        furtherInformationDatabase.open();
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

        int weekday = getWeekDayInfo();
        if (MONDAY <= weekday && weekday <= FRIDAY) {
            createTimeView(closestPool.getString(PARSE_OPEN_TIME), closestPool.getString(PARSE_CLOSE_TIME));
        } else if (weekday == SATURDAY) {
            createTimeView(closestPool.getString(PARSE_OPEN_TIME_SAT), closestPool.getString(PARSE_CLOSE_TIME_SAT));
        } else {
            createTimeView(closestPool.getString(PARSE_OPEN_TIME_SUN), closestPool.getString(PARSE_CLOSE_TIME_SUN));
        }

        createTimeView(closestPool.getString(PARSE_OPEN_TIME), closestPool.getString(PARSE_CLOSE_TIME));
        textPhoneNumber.setText(closestPool.getString(PARSE_PHONE_NUMBER));
        setPoolPicture(closestPool.getString(PARSE_PIC_PATH));
    }

    /* Method written with the help of: http://stackoverflow.com/questions/18600257/how-to-get-the-weekday-of-a-date
    http://stackoverflow.com/questions/8077530/android-get-current-timestamp and
    http://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java .*/
    private int getWeekDayInfo() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return weekday;
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
        furtherInformation = (ImageButton)findViewById(R.id.image_button_information);
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
        allComments.setOnClickListener(this);
        makeACommnent.setOnClickListener(this);
        furtherInformation.setOnClickListener(this);
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
            case R.id.button_show_all_comments:
                switchToAllCommentsActivity();
                break;
            case R.id.image_button_information:
                showFurtherInformationDialog();
                break;
            case R.id.button_make_a_comment:
                if (ParseUser.getCurrentUser() != null && checkIfConnectedToInternet() == true) {
                    showCommentDialog();
                } else if (checkIfConnectedToInternet() == false) {
                    showYouAreOfflineToast();
                }
                else {
                    showYouAreNotSignedInToast();
                }
                break;

        }

    }

    private void showFurtherInformationDialog() {
        if (dayTicket.equals("")) {
            showNoConnectionDialog();
        } else {
            showFurtherInformationDialogWithData();
        }

    }

    /* This method was created using http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog as a source. */
    private void showFurtherInformationDialogWithData() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_further_information, null);
        dialogBuilder.setView(dialogView);

        TextView priceForDay = (TextView)dialogView.findViewById(R.id.text_view_day_ticket);
        priceForDay.setText(DAY_TICKET_PRICE + dayTicket);
        TextView furtherInfoOnCivicPool = (TextView)dialogView.findViewById(R.id.text_view_info_on_civic_pool);
        String formattedInfoOnCivicPool = formatInfoOnCivicPoolString();
        furtherInfoOnCivicPool.setText(formattedInfoOnCivicPool);
        furtherInfoOnCivicPool.setLineSpacing(0, (float) LINE_SPACING);

        dialogBuilder.setTitle(PIECES_OF_INFORMATION);
        dialogBuilder.setPositiveButton(OKAY, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing since the dialog only closes
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showNoConnectionDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(PIECES_OF_INFORMATION);
        dialogBuilder.setPositiveButton(OKAY, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing since the dialog only closes
            }
        });
        dialogBuilder.setMessage(NO_CONTENT_AVAILABLE);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    // Created with the help of http://stackoverflow.com/questions/3429546/how-do-i-add-a-bullet-symbol-in-textview
    private String formatInfoOnCivicPoolString() {
        String result = infoOnCivicPool.replace(" ", "\n\u2022 ");
        return result;
    }

    private void showYouAreNotSignedInToast() {
        Toast.makeText(ClosestCivicPoolActivity.this, NOT_ALLOWED_TO_COMMENT, Toast.LENGTH_LONG).show();
    }

    private void showYouAreOfflineToast() {
        Toast.makeText(ClosestCivicPoolActivity.this, YOU_ARE_OFFLINE, Toast.LENGTH_LONG).show();
    }



    /* This method was created using http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog as a source. */
    private void showCommentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comment_rating, null);
        dialogBuilder.setView(dialogView);

        TextView poolName = (TextView)dialogView.findViewById(R.id.text_view_dialog_comment_civic_pool_name);
        poolName.setText(textName.getText().toString());
        TextView userName = (TextView)dialogView.findViewById(R.id.text_view_dialog_comment_username);
        userName.setText(ParseUser.getCurrentUser().getUsername());
        final EditText commentFromUser = (EditText)dialogView.findViewById(R.id.text_view_dialog_comment);
        final RatingBar ratingFromUser = (RatingBar)dialogView.findViewById(R.id.ratingbar_dialog_comment);

        dialogBuilder.setTitle(R.string.make_a_comment);
        dialogBuilder.setPositiveButton(R.string.submit_comment, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_submit_comment, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing since the dialog only closes
            }
        });
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        // Created with help from: http://stackoverflow.com/questions/27345584/how-to-prevent-alertdialog-to-close
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userComment = commentFromUser.getText().toString();
                int userRating = (int) ratingFromUser.getRating();
                boolean closeDialog = postObjectToParseBackend(userComment, userRating);
                if (closeDialog == true) {
                    dialog.dismiss();
                }
            }
        });
        if (listsize == 0) {
            listsize = 1;
        }
        averageRating.setRating((int)((sumOfRatings + ratingFromUser.getRating())/listsize));
    }

    /* This method was created using the official parse.com documentation as a source:
    * https://parse.com/docs/android/guide#objects .*/
    private boolean postObjectToParseBackend(String userComment, int userRating) {
        if (userComment.length() < MIN_COMMENT_LENGTH) {
            Toast.makeText(ClosestCivicPoolActivity.this, COMMENT_TOO_SHORT, Toast.LENGTH_LONG).show();
            return false;
        } else if (userComment.length() > MAX_COMMENT_LENGTH) {
            Toast.makeText(ClosestCivicPoolActivity.this, COMMENT_TOO_LONG, Toast.LENGTH_LONG).show();
            return false;
        } else if (userRating < MIN_RATING) {
            Toast.makeText(ClosestCivicPoolActivity.this, FORGOT_RATING, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ParseObject commentObject = new ParseObject(PARSE_COMMENT_RATING);
            commentObject.put(PARSE_USERNAME, ParseUser.getCurrentUser().getUsername());
            commentObject.put(PARSE_RATING, userRating);
            String date = fetchCurrentTime();
            commentObject.put(PARSE_DATE, date);
            commentObject.put(PARSE_CORRESPONDING_CIVIC_ID, closestPoolCivicID);
            commentObject.put(PARSE_COMMENT, userComment);
            commentObject.saveInBackground();
            CommentRating commentRating = new CommentRating(ParseUser.getCurrentUser().getUsername(),userComment,
                    closestPoolCivicID, userRating, date);
            updateLatestComment(commentRating);
            updateCivicPoolAverageRatingOnParse(userRating);
            return true;
        }
    }

    private void updateCivicPoolAverageRatingOnParse(final int userRating) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_CORRESPONDING_CIVIC_ID, closestPoolCivicID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    int score = 0;
                    for (int i = 0; i < list.size(); i++) {
                        score += (list.get(i)).getInt(PARSE_RATING);
                    }
                    listsize = list.size();
                    if (list.size() == 0) {
                        final float averageRatingParse = userRating;
                        Log.d("AVERAGERATINGSSS77", String.valueOf(averageRatingParse));
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CIVIC_POOL);
                        query.whereEqualTo(PARSE_CIVIC_ID, closestPoolCivicID);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    ParseObject object = list.get(0);
                                    Log.d("AVERAGERATINGSSS88", String.valueOf(averageRatingParse));
                                    object.put(PARSE_CURRENT_RATING, averageRatingParse);
                                    object.saveInBackground();
                                    averageRating.setRating(averageRatingParse);
                                }
                            }
                        });

                    } else {
                        Log.d("Lange", String.valueOf(list.size()));
                        final float averageRatingParse = (userRating + score)/(list.size() + 1);
                        Log.d("AVERAGERATINGSSS77", String.valueOf(averageRatingParse));
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CIVIC_POOL);
                        query.whereEqualTo(PARSE_CIVIC_ID, closestPoolCivicID);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    ParseObject object = list.get(0);
                                    Log.d("AVERAGERATINGSSS88", String.valueOf(averageRatingParse));
                                    object.put(PARSE_CURRENT_RATING, averageRatingParse);
                                    object.saveInBackground();
                                    averageRating.setRating(averageRatingParse);
                                }
                            }
                        });}

                }
            }
        });

    }



    private void updateLatestComment(CommentRating commentRating) {
        usernameComment.setText(commentRating.getUserName());
        ratingComment.setRating(commentRating.getRating());
        comment.setText(commentRating.getComment());
        dateComment.setText(commentRating.getDate());
    }

    /* This method was written using the resource http://stackoverflow.com/questions/8077530/android-get-current-timestamp
    * as a guideline. It gets the current time of the system. */
    private String fetchCurrentTime () {
        long time = System.currentTimeMillis();
        String formattedTime = formatTimeString(time);
        String formattedTimeString =
                formattedTime.substring(SUBSTRING_DAY_START, SUBSTRING_DAY_END) + "."
                        + formattedTime.substring(SUBSTRING_MONTH_START, SUBSTRING_MONTH_END) + "."
                        + formattedTime.substring(SUBSTRING_YEAR_START, SUBSTRING_YEAR_END) + "  "
                        + formattedTime.substring(SUBSTRING_TIME_START, SUBSTRING_TIME_END);
        Log.d("aktuelle Zeit", formattedTimeString);
        return formattedTimeString;
    }


    /* This method was written using the resource http://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java
    * as a guideline. It formats the unix time string into a human-readable format. */
    private String formatTimeString(long time) {
        Date todayDate = new Date (time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        String dateInFormat = simpleDateFormat.format(todayDate);
        return dateInFormat;
    }


    private void switchToAllCommentsActivity() {
        Intent switchToAllCommentsActivity = new Intent (ClosestCivicPoolActivity.this, AllCommentsActivity.class);
        switchToAllCommentsActivity.putExtra("id", closestPoolCivicID);
        startActivity(switchToAllCommentsActivity);
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
        goToMap.putExtra("latitude", poolLati);
        goToMap.putExtra("longitude", poolLongi);
        goToMap.putExtra("name", textName.getText().toString());
        goToMap.putExtra("origin", "detail");
        startActivity(goToMap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_closest_civic_pool, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();}
        if (id == R.id.action_myAccount) {
            changeToMyAccountActivity();
        }
        if (id == R.id.action_logout) {
            goBackToHomeScreen();
        }

        return super.onOptionsItemSelected(item);
    }


    // Created with the help of: https://parse.com/docs/android/guide#users
    private void goBackToHomeScreen() {
        ParseUser.logOut();
        Intent goBackToHomeScreen = new Intent (ClosestCivicPoolActivity.this, HomeScreenActivity.class);
        // From: http://stackoverflow.com/questions/6397576/finish-to-go-back-two-activities
        goBackToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackToHomeScreen);
    }

    private void changeToMyAccountActivity() {
        Intent changeToMyAccountActivity = new Intent (ClosestCivicPoolActivity.this, MyAccountActivity.class);
        startActivity(changeToMyAccountActivity);
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

        if(userLat == DEFAULT_LAT && userLong == DEFAULT_LONG){
            Toast.makeText(ClosestCivicPoolActivity.this, DEFAULT_LOCATION_TOAST, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataDistanceDataReceived(double dist) {
        textDistance.setText(" " + String.valueOf(dist) + " ");
        updateProgressBarStatus(PROGRESS_BAR_MAX);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        furtherInformationDatabase.close();
    }


}
