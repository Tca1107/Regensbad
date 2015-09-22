package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
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

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Domain.CommentRating;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.Persistence.FurtherInformationDatabase;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class CivicPoolDetailActivity extends ActionBarActivity implements DistanceDataProvider.DistanceDataReceivedListener, LocationUpdater.OnLocationUpdateReceivedListener,
View.OnClickListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";


    // Properties for location updates
    private static final int FIX_UPDATE_TIME = 500; // milliseconds
    private static final int FIX_UPDATE_DISTANCE = 5; // meters

    private static final int SCREEN_HEIGHT_DIVIDE_FACTOR = 10;
    private static final int SCREEN_HEIGHT_DIVIDE_FACTOR_EXTENDED = 20;
    private static final int SCREEN_MAX_HEIGHT = 1000;

    private static final double DEFAULT_LAT = 49.010259;
    private static final double DEFAULT_LONG = 12.100722;

    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_CORRESPONDING_CIVIC_ID = "correspondingCivicID";
    private static final String PARSE_USERNAME = "userName";
    private static final String PARSE_DATE = "date";
    private static final String PARSE_COMMENT = "comment";
    private static final String PARSE_RATING = "rating";
    private static final String PARSE_CREATED_AT = "createdAt";
    private static final String PARSE_CIVIC_POOL = "CivicPool";
    private static final String PARSE_CURRENT_RATING = "currentRating";
    private static final String PARSE_CIVIC_ID = "civicID";



    /* Weekdays */
    private static final int MONDAY = 2;
    private static final int FRIDAY = 6;
    private static final int SATURDAY = 7;
    private static final int SUNDAY = 1;

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

    /* Constants of the type String needed for the Toasts. */
    private static final String DEFAULT_LOCATION_TOAST = "Kein GPS-Empfang! Es wird der Regensburger Hauptbahnhof als Standort angenommen.";
    private static final String NOT_ALLOWED_TO_COMMENT = "Sie haben keinen Account oder sind nicht eingeloggt. Sie können daher keine Kommentare oder Bewertungen abgeben.";
    private static final String YOU_ARE_OFFLINE = "Sie sind nicht mit dem Internet verbunden. Sie können daher keine Kommentare oder Bewertungen abgeben.";
    private static final int MIN_COMMENT_LENGTH = 5;
    private static final int MAX_COMMENT_LENGTH = 250;
    private static final String COMMENT_TOO_SHORT = "Ein Kommentar muss mindestens fünf Zeichen umfassen.";
    private static final String COMMENT_TOO_LONG = "Ein Kommentar darf nicht mehr als 250 Zeichen umfassen.";
    private static final String FORGOT_RATING = "Bitte geben Sie eine Bewertung ab.";
    private static final int MIN_RATING = 1;


    private static final double LINE_SPACING = 1.2;

    private static final String OKAY = "OK";
    private static final String PIECES_OF_INFORMATION = "Weitere Informationen";
    private static final String DAY_TICKET_PRICE = "Tagesticket: ";
    private static final String NO_CONTENT_AVAILABLE = "Leider kein Inhalt verfügbar, da keine Verbindung zum Internet besteht.";


    private int ID;
    private double distance;
    private double userLat;
    private double userLong;

    private FurtherInformationDatabase furtherInformationDatabase;
    private String dayTicket;
    private String infoOnCivicPool;


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


    private DistanceDataProvider distanceDataProvider;
    private Database db;
    private CivicPool pool;
    private int listsize;
    private float sumOfRatings = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDatabase();
        initializeActionBar();
        getExtras();
        getDistance();
        initializeUIElements();
        registerOnClickListeners();
        setTheCurrentComment();
        initializeFurtherInformationDatabase();
    }

    private void registerOnClickListeners() {
        makeACommnent.setOnClickListener(this);
        allComments.setOnClickListener(this);
        showMapButton.setOnClickListener(this);
        startNavigationButton.setOnClickListener(this);
        textWebsite.setOnClickListener(this);
        textPhoneNumber.setOnClickListener(this);
        furtherInformation.setOnClickListener(this);
    }

    private void initializeFurtherInformationDatabase() {
        furtherInformationDatabase = new FurtherInformationDatabase(this);
        furtherInformationDatabase.open();
        dayTicket = furtherInformationDatabase.getDayTicket(ID);
        infoOnCivicPool = furtherInformationDatabase.getSports(ID);
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
        LocationUpdater locationUpdater = new LocationUpdater(Context.LOCATION_SERVICE, FIX_UPDATE_TIME, FIX_UPDATE_DISTANCE, this);
        locationUpdater.setLocationUpdateListener(this);
        locationUpdater.requestLocationUpdates();
        distanceDataProvider = new DistanceDataProvider();
        distanceDataProvider.setOnDistanceDataReceivedListener(this);
        String downloadString = "http://maps.googleapis.com/maps/api/directions/json?origin=" + userLat + "," + userLong + "&destination=" + pool.getLati() + "," + pool.getLongi() + "&mode=driving&sensor=false";
        distanceDataProvider.execute(downloadString);
    }

    private void initializeDatabase() {
        db = new Database(this);
        db.open();
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
        textDistance.setText(" " + String.valueOf(distance) + " ");
        textPhoneNumber.setText(pool.getPhoneNumber());
    }

    private void getTheElements() {
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

    /* Following four lines were created with the help of the following web resource:
    http://stackoverflow.com/questions/13105430/android-setting-image-from-string */
    private void setPoolPictureFromPathString() {
        setScreenHeight();
        String picPath = db.getPicPath(ID);
        int id = getResources().getIdentifier(picPath, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        poolPicture.setImageDrawable(drawable);
        poolPicture.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    // Created with the help of: http://stackoverflow.com/questions/22743153/android-device-screen-resolution
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

    private void createTimeView() {
        int weekday = getWeekDayInfo();
        if (MONDAY <= weekday && weekday <= FRIDAY) {
            String timeString = " " + pool.getOpenTime().substring(0,2) + ":" + pool.getOpenTime().substring(2) + " - " + pool.getCloseTime().substring(0, 2) + ":" + pool.getCloseTime().substring(2);
            textOpenTime.setText(timeString);
        } else if (weekday == SATURDAY) {
            String timeString = " " + pool.getOpenTimeSat().substring(0,2) + ":" + pool.getOpenTimeSat().substring(2) + " - " + pool.getCloseTimeSat().substring(0, 2) + ":" + pool.getCloseTimeSat().substring(2);
            textOpenTime.setText(timeString);
        } else {
            String timeString = " " + pool.getOpenTimeSun().substring(0,2) + ":" + pool.getOpenTimeSun().substring(2) + " - " + pool.getCloseTimeSun().substring(0, 2) + ":" + pool.getCloseTimeSun().substring(2);
            textOpenTime.setText(timeString);
        }


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


    /* This method retrieves the latest CommentRating Object from parse.com It was written using the parse.com documentation at:
  https://parse.com/docs/android/guide#objects-retrieving-objects
  https://parse.com/docs/android/guide#queries .*/
    private void setTheCurrentComment () {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_CORRESPONDING_CIVIC_ID, ID);
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
        String date = "";
        if (list.size() > 0) {
            ParseObject currentObject = list.get(0);
            usernameComment.setText(currentObject.getString(PARSE_USERNAME));
            comment.setText(currentObject.getString(PARSE_COMMENT));
            dateComment.setText(currentObject.getString(PARSE_DATE));
            ratingComment.setRating((int) currentObject.getNumber(PARSE_RATING));
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_civic_pool_detail, menu);
            return super.onCreateOptionsMenu(menu);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
        finish();}
        if (id == R.id.action_myAccount){
            changeToMyAccountActivity();
        }
        if (id == R.id.action_logout){
            goBackToHomeScreen();
        }

        return super.onOptionsItemSelected(item);
    }

    // from https://parse.com/docs/android/guide#users
    private void goBackToHomeScreen() {
        ParseUser.logOut();
        Intent goBackToHomeScreen = new Intent (CivicPoolDetailActivity.this, HomeScreenActivity.class);
        // From: http://stackoverflow.com/questions/6397576/finish-to-go-back-two-activities
        goBackToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackToHomeScreen);
    }

    private void changeToMyAccountActivity() {
        Intent changeToMyAccountActivity = new Intent (CivicPoolDetailActivity.this, MyAccountActivity.class);
        startActivity(changeToMyAccountActivity);
    }


    @Override
    public void onDataDistanceDataReceived(double dist) {
        distance = dist;
        textDistance.setText(" " + String.valueOf(distance) + " ");
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
            Toast.makeText(CivicPoolDetailActivity.this, DEFAULT_LOCATION_TOAST, Toast.LENGTH_LONG).show();
        }
    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_make_a_comment:
                if (ParseUser.getCurrentUser() != null && checkIfConnectedToInternet() == true) {
                    showCommentDialog();
                } else if (checkIfConnectedToInternet() == false) {
                    showYouAreOfflineToast();
                } else {
                    showYouAreNotSignedInToast();
                }
                break;
            case R.id.button_show_all_comments:
                switchToAllCommentsActivity();
                break;
            case R.id.text_website:
                goToWebsite();
                break;
            case R.id.button_nav:
                startNavigation();
                break;
            case R.id.button_showOnMap:
                goToMap();
                break;
            case R.id.text_phoneNumber:
                makeACall();
                break;
            case R.id.image_button_information:
                showFurtherInformationDialog();
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
        Toast.makeText(CivicPoolDetailActivity.this, NOT_ALLOWED_TO_COMMENT, Toast.LENGTH_LONG).show();
    }

    private void showYouAreOfflineToast() {
        Toast.makeText(CivicPoolDetailActivity.this, YOU_ARE_OFFLINE, Toast.LENGTH_LONG).show();
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
            Toast.makeText(CivicPoolDetailActivity.this, COMMENT_TOO_SHORT, Toast.LENGTH_LONG).show();
            return false;
        } else if (userComment.length() > MAX_COMMENT_LENGTH) {
            Toast.makeText(CivicPoolDetailActivity.this, COMMENT_TOO_LONG, Toast.LENGTH_LONG).show();
            return false;
        } else if (userRating < MIN_RATING) {
            Toast.makeText(CivicPoolDetailActivity.this, FORGOT_RATING, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ParseObject commentObject = new ParseObject(PARSE_COMMENT_RATING);
            commentObject.put(PARSE_USERNAME, ParseUser.getCurrentUser().getUsername());
            commentObject.put(PARSE_RATING, userRating);
            String date = fetchCurrentTime();
            commentObject.put(PARSE_DATE, date);
            commentObject.put(PARSE_CORRESPONDING_CIVIC_ID, ID);
            commentObject.put(PARSE_COMMENT, userComment);
            commentObject.saveInBackground();
            CommentRating commentRating = new CommentRating(ParseUser.getCurrentUser().getUsername(),userComment,
                    ID, userRating, date);
            updateLatestComment(commentRating);
            updateCivicPoolAverageRatingOnParse(userRating);
            return true;
        }
    }

    private void updateCivicPoolAverageRatingOnParse(final int userRating) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_CORRESPONDING_CIVIC_ID, ID);
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
                        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CIVIC_POOL);
                        query.whereEqualTo(PARSE_CIVIC_ID, ID);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    ParseObject object = list.get(0);
                                    object.put(PARSE_CURRENT_RATING, averageRatingParse);
                                    object.saveInBackground();
                                    averageRating.setRating(averageRatingParse);
                                }
                            }
                        }); }
                    else {
                    final float averageRatingParse = (userRating + score)/(list.size() + 1);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CIVIC_POOL);
                    query.whereEqualTo(PARSE_CIVIC_ID, ID);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                ParseObject object = list.get(0);
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


    private void makeACall() {
        //From: http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically and http://developer.android.com/reference/android/content/Intent.html
        Intent makeCall = new Intent(Intent.ACTION_DIAL);
        makeCall.setData(Uri.parse("tel:" + pool.getPhoneNumber()));
        startActivity(makeCall);
    }

    private void goToWebsite() {
        //From: http://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
        Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(pool.getWebsite()));
        startActivity(startBrowser);
    }

    private void startNavigation() {
        //From: http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
        Intent startNavigationIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + pool.getLati() + "," + pool.getLongi()));
        startActivity(startNavigationIntent);
    }

    private void goToMap() {
        Intent goToMap = new Intent(CivicPoolDetailActivity.this, MapsActivity.class);
        goToMap.putExtra("ID", pool.getID());
        goToMap.putExtra("origin", "detail");
        startActivity(goToMap);
    }


    private void switchToAllCommentsActivity() {
        Intent switchToAllCommentsActivity = new Intent (CivicPoolDetailActivity.this, AllCommentsActivity.class);
        switchToAllCommentsActivity.putExtra("id", ID);
        startActivity(switchToAllCommentsActivity);
    }
}
