package com.example.tom.regensbad.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tom.regensbad.R;
import com.parse.ParseUser;


public class HomeScreenActivity extends ActionBarActivity implements View.OnClickListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    private static final String KEY_FOR_INTENT_EXTRA = "query";

    private TextView appName;
    private Button buttonClosestLake;
    private Button buttonGoToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeUIElements();
        initializeActionBar();
        registerOnClickListeners();
        //initializeSearchForCivicPools();


    }

    private void registerOnClickListeners() {
        buttonClosestLake.setOnClickListener(this);
        buttonGoToList.setOnClickListener(this);
    }


    /*
    /* This method was written using the tutorial "Creating a Search Interface", which is available at
     * http://developer.android.com/guide/topics/search/search-dialog.html .
    private void initializeSearchForCivicPools() {
        searchForCivicPools.setSubmitButtonEnabled(true);
        searchForCivicPools.setIconifiedByDefault(true);
        searchForCivicPools.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                changeToSearchCivicPoolsActivity(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
*/


    private void changeToSearchCivicPoolsActivity(String query) {
        Intent changeToSearchCivicPoolsActivity = new Intent(HomeScreenActivity.this, SearchCivicPoolsActivity.class);
        changeToSearchCivicPoolsActivity.putExtra(KEY_FOR_INTENT_EXTRA, query);
        startActivity(changeToSearchCivicPoolsActivity);
    }


    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
    with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .*/
    private void initializeActionBar() {
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.home_screen_action_bar, null);
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        ((TextView)view.findViewById(R.id.text_view_action_bar_home_screen)).setTypeface(typeface);
        this.getSupportActionBar().setCustomView(view);
    }



    private void initializeUIElements() {
        appName = (TextView)findViewById(R.id.text_view_app_name);
        setFontOfAppName();
        buttonClosestLake = (Button)findViewById(R.id.button_closest_lake);
        buttonGoToList = (Button)findViewById(R.id.button_goToList);
    }

    private void setFontOfAppName() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        appName.setTypeface(typeface);
    }




    @Override
    /* Depending on whether a user is signed in, this method loads the respective menu resource file. */
      /* http://developer.android.com/guide/topics/search/search-dialog.html .*/
    /* http://developer.android.com/guide/topics/ui/actionbar.html#ActionView */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            getMenuInflater().inflate(R.menu.menu_home_screen, menu);
            addTheSearchView(menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_home_screen_user_not_signed_in, menu);
            addTheSearchView(menu);
            return super.onCreateOptionsMenu(menu);
        }
    }


    /* http://developer.android.com/guide/topics/search/search-dialog.html .*/
    /* http://developer.android.com/guide/topics/ui/actionbar.html#ActionView */
    private void addTheSearchView(Menu menu) {
        //SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_button_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
    }

    /* http://developer.android.com/guide/topics/search/search-dialog.html .*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_myAccount) {
            return true;
        }else if (id == R.id.action_logout){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // buttons zu activities
    }
}
