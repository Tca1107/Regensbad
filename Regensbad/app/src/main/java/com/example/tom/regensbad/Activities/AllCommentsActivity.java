package com.example.tom.regensbad.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tom.regensbad.Adapters.CommentAdapter;
import com.example.tom.regensbad.Domain.CommentRating;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AllCommentsActivity extends ActionBarActivity {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_CORRESPONDING_CIVIC_ID = "correspondingCivicID";
    private static final String PARSE_USERNAME = "userName";
    private static final String PARSE_DATE = "date";
    private static final String PARSE_COMMENT = "comment";
    private static final String PARSE_RATING = "rating";
    private static final String PARSE_CREATED_AT = "createdAt";


    private ListView allCommentsList;
    private ArrayList<CommentRating> commentRatingsArrayList = new ArrayList<CommentRating>();
    private CommentAdapter commentAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUIElements();
        initializeActionBar();
        fetchCommentsFromParse();
        initializeAdapter();
    }


    private void initializeAdapter() {
        commentAdapter = new CommentAdapter(this, commentRatingsArrayList);
        allCommentsList.setAdapter(commentAdapter);
    }

    private void fetchCommentsFromParse() {
        getDataForLatestComment();
    }

    /* This method retrieves the latest CommentRating Object from parse.com It was written using the parse.com documentation at:
   https://parse.com/docs/android/guide#objects-retrieving-objects
   https://parse.com/docs/android/guide#queries .*/
    private void getDataForLatestComment() {
        int poolID = getPoolID();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_CORRESPONDING_CIVIC_ID, poolID);
        // following line from http://stackoverflow.com/questions/27971733/how-to-get-latest-updated-parse-object-in-android
        query.orderByDescending(PARSE_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null) {
                    convertParseObjectsToCommentObjectsAndAddThemToArrayList(list);
                }
            }
        });

    }


    /* Converts the retrieved parse objects into CommentRating objects and adds the to the arraylist that the adapter works with. */
    private void convertParseObjectsToCommentObjectsAndAddThemToArrayList(List<ParseObject> list) {
        commentRatingsArrayList.clear();
        for (int i = 0; i < list.size(); i++) {
            ParseObject currentObject = list.get(i);
            String userName = currentObject.getString(PARSE_USERNAME);
            String comment = currentObject.getString(PARSE_COMMENT);
            int rating = (int)currentObject.getNumber(PARSE_RATING);
            int correspondingCivicID = (int)currentObject.getNumber(PARSE_CORRESPONDING_CIVIC_ID);
            String date = currentObject.getString(PARSE_DATE);
            CommentRating commentRating = new CommentRating(userName, comment, correspondingCivicID, rating, date);
            commentRatingsArrayList.add(commentRating);
        }
        commentAdapter.notifyDataSetChanged();

    }

    private int getPoolID() {
        Bundle extras = getIntent().getExtras();
        return extras.getInt("id");
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

    private void initializeUIElements() {
        setContentView(R.layout.activity_all_comments);
        allCommentsList = (ListView)findViewById(R.id.listview_all_comments);
    }

    private void getIntentExtras() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_user_online, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_all_comments, menu);
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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_myAccount) {
            changeToMyAccountActivity();
        }
        if (id == R.id.action_logout) {
            goBackToHomeScreen();
        }

        return super.onOptionsItemSelected(item);
    }

    // from https://parse.com/docs/android/guide#users
    private void goBackToHomeScreen() {
        ParseUser.logOut();
        Intent goBackToHomeScreen = new Intent (AllCommentsActivity.this, HomeScreenActivity.class);
        // From: http://stackoverflow.com/questions/6397576/finish-to-go-back-two-activities
        goBackToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackToHomeScreen);
    }

    private void changeToMyAccountActivity() {
        Intent changeToMyAccountActivity = new Intent (AllCommentsActivity.this, MyAccountActivity.class);
        startActivity(changeToMyAccountActivity);
    }

}
