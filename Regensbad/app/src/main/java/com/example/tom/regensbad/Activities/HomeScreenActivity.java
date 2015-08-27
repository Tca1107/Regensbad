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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.Weather;
import com.example.tom.regensbad.Persistence.WeatherDataProvider;
import com.example.tom.regensbad.R;
import com.parse.ParseUser;


public class HomeScreenActivity extends ActionBarActivity implements View.OnClickListener, WeatherDataProvider.WeatherDataReceivedListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    /* Constant of the type string defining the key for the intent extras. */
    private static final String KEY_FOR_INTENT_EXTRA = "query";

    private static final String CELSIUS= "°C";

    private static final int WEATHER_DIVISION_CONSTANT = 100;
    private static final int WEATHER_ID_THUNDERSTORM = 2;
    private static final int WEATHER_ID_LIGHT_RAIN = 3;
    private static final int WEATHER_ID_A_LOT_OF_RAIN = 5;
    private static final int WEATHER_ID_SNOW = 6;
    private static final int WEATHER_ID_FOG = 7;
    private static final int WEATHER_ID_SUNNY_AND_CLOUDY = 8;
    private static final int WEATHER_EXTENDED_ID_SUNNY = 800;
    private static final int WEATHER_EXTENDED_ID_SUNNY_WITH_VERY_FEW_CLOUDS = 801;


    /* Constant of the type string defining the web resource from which the JSON array will be downloaded from.
    * This is provided by the API of www.openweathermap.org . Here, one can get information on the current weather in Regensburg
    * for free. */
    private static final String WEB_ADDRESS_TO_RETRIEVE_WEATHER_DATA = "http://api.openweathermap.org/data/2.5/weather?q=Regensburg,germany&lang=de&units=metric";



    private TextView degrees;
    private TextView weatherDescription;
    private ImageView weatherIcon;
    private TextView appName;
    private Button buttonClosestLake;
    private Button buttonGoToList;
    private WeatherDataProvider weatherDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeUIElements();
        initializeActionBar();
        initializeDownLoadOfWeatherData();
        registerOnClickListeners();

    }

    private void initializeDownLoadOfWeatherData() {
        weatherDataProvider = new WeatherDataProvider();
        weatherDataProvider.setOnWeatherDataReceivedListener(this);
        weatherDataProvider.execute(WEB_ADDRESS_TO_RETRIEVE_WEATHER_DATA);
    }

    private void registerOnClickListeners() {
        buttonClosestLake.setOnClickListener(this);
        buttonGoToList.setOnClickListener(this);
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
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }

        degrees = (TextView)findViewById(R.id.text_view_weather_degrees);
        weatherDescription = (TextView)findViewById(R.id.text_view_weather_description);
        weatherIcon = (ImageView)findViewById(R.id.image_view_weather_icon);
        appName = (TextView)findViewById(R.id.text_view_app_name);
        setFontOfAppName();
        buttonClosestLake = (Button)findViewById(R.id.button_closest_lake);
        buttonGoToList = (Button)findViewById(R.id.button_goToList);
    }

    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = HomeScreenActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // hier mit Samsung Galaxy S2 testen
        window.setStatusBarColor(HomeScreenActivity.this.getResources().getColor(R.color.blue_dark_primary_color));
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
        MenuItem searchItem = menu.findItem(R.id.action_button_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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



    private void changeToSearchCivicPoolsActivity(String query) {
        Intent changeToSearchCivicPoolsActivity = new Intent(HomeScreenActivity.this, SearchCivicPoolsActivity.class);
        changeToSearchCivicPoolsActivity.putExtra(KEY_FOR_INTENT_EXTRA, query);
        startActivity(changeToSearchCivicPoolsActivity);
    }



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
            // from https://parse.com/docs/android/guide#users
            ParseUser.logOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_closest_lake:
                changeToClosestCivicPoolActivity();
                break;
            case R.id.button_goToList:
                changeToAllCivicPoolsActivity();
                break;
        }
    }

    private void changeToAllCivicPoolsActivity() {
        Intent changeToAllCivicPoolsActivity = new Intent (HomeScreenActivity.this, AllCivicPoolsActivity.class);
        startActivity(changeToAllCivicPoolsActivity);
    }

    private void changeToClosestCivicPoolActivity() {
        Intent changeToClosestCivicPoolActivity = new Intent (HomeScreenActivity.this, ClosestCivicPoolActivity.class);
        startActivity(changeToClosestCivicPoolActivity);
    }

    @Override
    public void onDataWeatherDataReceived(Weather weather) {
        Log.d("Communication", "worked");
        if (weather != null) {
            degrees.setText(weather.getDegrees() + CELSIUS);
            weatherDescription.setText(weather.getWeatherDescription());
            assignWeatherIcon(weather.getweatherIcon());

            // hier dann noch die Methode für die Icons!!
        } else {

        }
    }


    /* This method/algorithm was written using the tutorial being accessible at the following website:
    * http://code.tutsplus.com/tutorials/create-a-weather-app-on-android--cms-21587 .
    * The API sends a three-digit value, the first digit of which decides whether it is sunny, cloudy, etc.
    * Values between 200 and 299 represent thunderstorms, for instance (for more examples, look at the constants defined above).
    * The following two digits define the weather in a more detailed manner. However, since in this application
    * only a limited amount of weather icons is used and needed, only the first digit is of interest, which is why
    * the weatherIconID is divided by 100. On the basis of the resulting one-digit value, a fitting icon is assigned to the
    * user interface.
    * However, there is also a value, namely 800, standing for a blue wky without any clouds, which needs to be checked explicitly.
    * This is done at the very beginning of the method.*/
    private void assignWeatherIcon(String weatherIconID) {
        int weatherID = Integer.valueOf(weatherIconID);
        int reducedweatherIconID = (weatherID / WEATHER_DIVISION_CONSTANT);
        if (weatherID == WEATHER_EXTENDED_ID_SUNNY) {
            weatherIcon.setImageResource(R.drawable.ic_weather_sun_no_clouds);
        } else if (weatherID == WEATHER_EXTENDED_ID_SUNNY_WITH_VERY_FEW_CLOUDS){
            weatherIcon.setImageResource(R.drawable.ic_weather_sunny_very_few_clouds);
        }
        else {
            switch (reducedweatherIconID) {
                case WEATHER_ID_THUNDERSTORM:
                    weatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                    break;
                case WEATHER_ID_FOG:
                    weatherIcon.setImageResource(R.drawable.ic_weather_many_clouds);
                    break;
                case WEATHER_ID_SNOW:
                    weatherIcon.setImageResource(R.drawable.ic_weather_snow);
                    break;
                case WEATHER_ID_LIGHT_RAIN:
                    weatherIcon.setImageResource(R.drawable.ic_weather_rain);
                    break;
                case WEATHER_ID_A_LOT_OF_RAIN:
                    weatherIcon.setImageResource(R.drawable.ic_waether_a_lot_of_rain);
                    break;
                case WEATHER_ID_SUNNY_AND_CLOUDY:
                    weatherIcon.setImageResource(R.drawable.ic_weather_sun_with_clouds);
           }

        }
    }
}
