package com.example.tom.regensbad.Activities;

import android.content.Context;
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
    private static final String PARSE_UP_VOTES = "upVotes";



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
        // registerOnClickListener();
    }


    /*
    private void registerOnClickListener() {
        allCommentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ImageView thumbsUp = (ImageView)view.findViewById(R.id.image_view_thumbs_up);
                thumbsUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentRating commentRating = commentRatingsArrayList.get(position);
                        commentRating.setIsLiked(true);
                        commentAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }
*/

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
        Log.d("POOLID", String.valueOf(poolID));
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

    private void convertParseObjectsToCommentObjectsAndAddThemToArrayList(List<ParseObject> list) {
        commentRatingsArrayList.clear();
        for (int i = 0; i < list.size(); i++) {
            ParseObject currentObject = list.get(i);
            String userName = currentObject.getString(PARSE_USERNAME);
            String comment = currentObject.getString(PARSE_COMMENT);
            int rating = (int)currentObject.getNumber(PARSE_RATING);
            int correspondingCivicID = (int)currentObject.getNumber(PARSE_CORRESPONDING_CIVIC_ID);
            String date = currentObject.getString(PARSE_DATE);
            int upVotes = currentObject.getInt(PARSE_UP_VOTES);
            CommentRating commentRating = new CommentRating(userName, comment, correspondingCivicID, rating, date, upVotes, false);
            commentRatingsArrayList.add(commentRating);
        }
        Log.d("arrayList", String.valueOf(commentRatingsArrayList));
        commentAdapter.notifyDataSetChanged();

    }

    private int getPoolID() {
        Bundle extras = getIntent().getExtras();
        return extras.getInt("id");
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
