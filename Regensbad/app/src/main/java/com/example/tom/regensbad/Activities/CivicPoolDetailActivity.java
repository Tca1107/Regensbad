package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.Persistence.DistanceDataProvider;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


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

    private String defaultLocationToast = "Kein GPS-Empfang! Es wird der Regensburger Hauptbahnhof als Standort angenommen.";

    private int ID;
    private double distance;
    private double userLat;
    private double userLong;


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
        registerOnClickListeners();
        setTheCurrentComment();
        handleInput();
    }

    private void registerOnClickListeners() {
        makeACommnent.setOnClickListener(this);
        allComments.setOnClickListener(this);
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
                //From: http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically and http://developer.android.com/reference/android/content/Intent.html
                Intent makeCall = new Intent(Intent.ACTION_DIAL);
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
        String timeString = " " + pool.getOpenTime().substring(0,2) + ":" + pool.getOpenTime().substring(2) + " - " + pool.getCloseTime().substring(0, 2) + ":" + pool.getCloseTime().substring(2);
        textOpenTime.setText(timeString);
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

        if(userLat == DEFAULT_LAT && userLong == DEFAULT_LONG){
            Toast.makeText(CivicPoolDetailActivity.this, defaultLocationToast, Toast.LENGTH_LONG).show();
        }
    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_show_all_comments:
                switchToAllCommentsActivity();
                break;

        }
    }

    private void switchToAllCommentsActivity() {
        Intent switchToAllCommentsActivity = new Intent (CivicPoolDetailActivity.this, AllCommentsActivity.class);
        switchToAllCommentsActivity.putExtra("id", ID);
        startActivity(switchToAllCommentsActivity);
    }
}
