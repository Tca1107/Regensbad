package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import com.example.tom.regensbad.LocationService.LocationUpdater;
import com.example.tom.regensbad.Persistence.FurtherInformationDatabase;
import com.example.tom.regensbad.Persistence.WeatherDataProvider;
import com.example.tom.regensbad.Persistence.WeatherLastUpdateDataProvider;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class HomeScreenActivity extends ActionBarActivity implements View.OnClickListener, WeatherDataProvider.WeatherDataReceivedListener, LocationUpdater.OnLocationUpdateReceivedListener {

    /* The weather icons used in this activity are taken from the website: http://www.iconarchive.com/tag/weather
    * They are free to use (GNU Lesser General Public Licence), even commercially. */

    /* In the application "Regensbad", the icons used for user interface elements, such as buttons, were taken from the website:
    * https://www.google.com/design/icons/ . There they are freely available. */

    /* In the application "Regensbad", the font "Pacifico" used for the name "Regensbad" was taken from the following website:
    * https://www.google.com/fonts/ .
    * There it is freely available. */

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    /* Constant of the type string defining the key for the intent extras. */
    private static final String KEY_FOR_INTENT_EXTRA = "query";

    private static final String CELSIUS= "°";

    /* Weather constants to figure out which kind of icon is needed. */
    private static final int WEATHER_DIVISION_CONSTANT = 100;
    private static final int WEATHER_ID_THUNDERSTORM = 2;
    private static final int WEATHER_ID_LIGHT_RAIN = 3;
    private static final int WEATHER_ID_A_LOT_OF_RAIN = 5;
    private static final int WEATHER_ID_SNOW = 6;
    private static final int WEATHER_ID_FOG = 7;
    private static final int WEATHER_ID_SUNNY_AND_CLOUDY = 8;
    private static final int WEATHER_EXTENDED_ID_SUNNY = 800;
    private static final int WEATHER_EXTENDED_ID_SUNNY_WITH_VERY_FEW_CLOUDS = 801;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final String TIME_ZONE = "CET"; // Central European Time

    /* Constant of the type string defining the web resource from which the JSON array will be downloaded from.
    * This is provided by the API of www.openweathermap.org . Here, one can get information on the current weather in Regensburg
    * for free. */
    private static final String WEB_ADDRESS_TO_RETRIEVE_WEATHER_DATA = "http://api.openweathermap.org/data/2.5/weather?q=Regensburg,germany&lang=de&units=metric";


    private static final int SUBSTRING_START_HOURS = 11;
    private static final int SUBSTRING_END_HOURS = 13;
    private static final int SUBSTRING_START_MINUTES = 14;
    private static final int SUBSTRING_END_MINUTES = 16;

    private static final int TIME_FACTOR = 1000;

    /*Constant for the download fromt he parse backend. */
    private static final String PARSE_FURTHER_INFORMATION = "FurtherInformation";
    private static final String PARSE_CORRESPONDING_CIVIC_ID = "correspondingCivicID";
    private static final String PARSE_SPORTS = "sports";
    private static final String PARSE_DAY_TICKET = "dayTicket";


    /* UI elements*/
    private TextView cityName;
    private TextView maxDegrees;
    private TextView minDegrees;
    private TextView lastUpdated;
    private TextView degrees;
    private TextView weatherDescription;
    private ImageView weatherIcon;
    private TextView appName;
    private Button buttonClosestLake;
    private Button buttonGoToList;
    private WeatherDataProvider weatherDataProvider;
    private WeatherLastUpdateDataProvider weatherLastUpdateDataProvider;
    private FurtherInformationDatabase furtherInformationDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeUIElements();
        initializeActionBar();
        initializeWeatherLastUpdateDataProvider();
        initializeDownLoadOfWeatherData();
        registerOnClickListeners();
        fillFurtherInformationDatabase();
    }

    /* This method downloads all the data from the FurtherInformation class from the parse backend.
    * This is later displayed in the CivicPoolDetailActivity and the ClosesCivicPoolActivity.
    * But as the parse methods work with callback methods which take some time to load all the data,
    * this is done here in the HomeScreenActivity in order to fill the database
    * already before the respective activities are called. When needed, the information is then in the mentioned activities only
    * read from the database. Of course, all this is only possible if the system is connected to the internet.  */
    private void fillFurtherInformationDatabase() {
        furtherInformationDatabase = new FurtherInformationDatabase(this);
        furtherInformationDatabase.open();
        if (checkIfConnectedToInternet() == true) {
            furtherInformationDatabase.deleteAllFurtherInformationItems();
            fetchFurtherInformationFromParse();
        }
    }

    /* This method retrieves the civic pool objects from the parse.com backend and adds them
    to the array list, that the adapter is set on. It was written using the parse.com documentation at:
    https://parse.com/docs/android/guide#objects-retrieving-objects
     https://parse.com/docs/android/guide#queries .*/
    private void fetchFurtherInformationFromParse() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_FURTHER_INFORMATION);
            query.whereExists(PARSE_CORRESPONDING_CIVIC_ID);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        saveFurtherInformationDataToDatabase(list);
                    }
                }
            });
        }

    /* All the objects from the list of objects that the parse callback method returns are asked for their respective data.
     * Out of this data a new FurtherInformation instance is created which is then added to the FurtheriInfromation database.  */
    private void saveFurtherInformationDataToDatabase(List<ParseObject> list) {
        for (int i = 0; i < list.size(); i++) {
            ParseObject furtherInformationFromParse = list.get(i);
            int civicID = furtherInformationFromParse.getInt(PARSE_CORRESPONDING_CIVIC_ID);
            String dayTicket = furtherInformationFromParse.getString(PARSE_DAY_TICKET);
            String sports = furtherInformationFromParse.getString(PARSE_SPORTS);
            furtherInformationDatabase.addFurtherInformationItem(civicID, dayTicket, sports);
        }
    }


    /* Initializes the WeatherLastUpdateProvider, that is to say the database in which the latest weather data is stored. */
    private void initializeWeatherLastUpdateDataProvider() {
        weatherLastUpdateDataProvider = new WeatherLastUpdateDataProvider(this);
        weatherLastUpdateDataProvider.open();
    }

    /* Initializes the download of the weather data (in form of a JSON object) from openweathermap.org.
    In doing so, the system uses an AsyncTask.
    * The present activity is registered as a listener and thus contains the fitting interface methods (see onDataWeatherDataReceived).
    * Whenever the download is finished, the HomeScreenActivity is informed about the change of data in the mentioned
    * interface method. Thus, the Observer Pattern applies here. Just as in the case with the location updater.
    * */
    private void initializeDownLoadOfWeatherData() {
        if (checkIfConnectedToInternet () == true) {
            weatherDataProvider = new WeatherDataProvider();
            weatherDataProvider.setOnWeatherDataReceivedListener(this);
            weatherDataProvider.execute(WEB_ADDRESS_TO_RETRIEVE_WEATHER_DATA);
        } else {
            fetchLatestUpdateFromLatestUpdateDataProvider();
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


    /* Gets the data for the weather "widget" from the respective database. */
    private void fetchLatestUpdateFromLatestUpdateDataProvider() {
        Weather weather = weatherLastUpdateDataProvider.getLatestUpdate();
        if (weather != null) {
        setWeatherDataForHomeScreen(weather);
        } else {
            // Intentionally left empty. If there is no internet connection the dummy texts will be loaded, which inform the user about
            // the fact that no internet connetion is available.
        }
    }

    private void registerOnClickListeners() {
        buttonClosestLake.setOnClickListener(this);
        buttonGoToList.setOnClickListener(this);
    }


    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
    with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .
     It sets up the action bar and loads the respective xml file with the help of a layout inflater.*/
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
        cityName = (TextView)findViewById(R.id.text_view_weather_city);
        maxDegrees = (TextView)findViewById(R.id.text_view_max_degrees);
        minDegrees = (TextView)findViewById(R.id.text_view_min_degrees);
        lastUpdated = (TextView)findViewById(R.id.text_view_weather_last_updated);
        degrees = (TextView)findViewById(R.id.text_view_weather_degrees);
        weatherDescription = (TextView)findViewById(R.id.text_view_weather_description);
        weatherIcon = (ImageView)findViewById(R.id.image_view_weather_icon);
        appName = (TextView)findViewById(R.id.text_view_app_name);
        setFontOfAppName();
        buttonClosestLake = (Button)findViewById(R.id.button_closest_lake);
        buttonGoToList = (Button)findViewById(R.id.button_goToList);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = HomeScreenActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // hier mit Samsung Galaxy S2 testen
        window.setStatusBarColor(HomeScreenActivity.this.getResources().getColor(R.color.blue_dark_primary_color));
    }


    /* This method was created using the tutorial on including external fonts in Android Studio which can be found
    * at the following website: http://www.thedevline.com/2014/03/how-to-include-fonts-in-android.html .
    * The font used is a font of Google Fonts named "Pacifico", which can be found at the following website:
    * https://www.google.com/fonts/ .*/
    private void setFontOfAppName() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        appName.setTypeface(typeface);
    }


    @Override
    /* Depending on whether a user is signed in and on whether the system is connected to the internet,*/
    /* this method loads the respective menu resource file.*/
    /* This method was created using the following to resources as guidelines:*/
    /* http://developer.android.com/guide/topics/search/search-dialog.html .*/
    /* http://developer.android.com/guide/topics/ui/actionbar.html#ActionView */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_home_screen, menu);
            // addTheSearchView(menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_home_screen_user_not_signed_in, menu);
            // addTheSearchView(menu);
            return super.onCreateOptionsMenu(menu);
        }
    }




    // The following method would add a searchview in the action bar. Initially we wanted to add search functionality to the application.
    // Due to the lack of time we were, however, not able to implement this kind of functionality.
    // We wanted to leave the respective methods in the code, still, because we could reactivate it when we have time for it.
    // For further information, please consult our "Projektbericht".


    /* This method was created using the following to resources as guidelines:
    /* http://developer.android.com/guide/topics/search/search-dialog.html */
    /* http://developer.android.com/guide/topics/ui/actionbar.html#ActionView .
    private void addTheSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_button_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (weatherLastUpdateDataProvider.getLatestUpdateTime().equals("")) {
                    changeToNoDataAvailableActivity();
                } else {
                    changeToSearchCivicPoolsActivity(query);
                }
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


    */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_myAccount) {
            changeToMyAccountActivity();
            return true;
        } else if (id == R.id.action_logout){
            // from https://parse.com/docs/android/guide#users
            ParseUser.logOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeToMyAccountActivity() {
        Intent changeToMyAccountActivity = new Intent (HomeScreenActivity.this, MyAccountActivity.class);
        startActivity(changeToMyAccountActivity);
    }


    /* Handles the clicks on the buttons. If there is no connection ot the internet and the database is empty,
    * which means that the user uses the pp for the very first time and is not connected to the internet,
    * the NoDataAvailableActivity is loaded. This is a very unlikely case. However, in doing so, we wanted to preclude the app from crashing or the user
    * being left with no fitting feedback. */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_closest_lake:
                if (weatherLastUpdateDataProvider.getLatestUpdateTime().equals("")) {
                    changeToNoDataAvailableActivity();
                } else {
                    changeToClosestCivicPoolActivity();
                }
                break;
            case R.id.button_goToList:
                if (weatherLastUpdateDataProvider.getLatestUpdateTime().equals("")) {
                    changeToNoDataAvailableActivity();
                } else {
                    changeToAllCivicPoolsActivity();
                }
                break;
        }
    }

    private void changeToNoDataAvailableActivity() {
        Intent changeToNoDataAvailableActivity = new Intent (HomeScreenActivity.this, NoDataAvailableActivity.class);
        startActivity(changeToNoDataAvailableActivity);
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
        if (weather != null) {
            insertWeatherDataInLastUpdateProvider(weather);
            setWeatherDataForHomeScreen(weather);
        } else {
            // Intentionally left blank.
        }
    }

    /* Sets the data which was received either from the database or most likely from the respective openweathermap.org API to the user interface elements.
     * Moreover, it calls the assignWeatherIcon method which, depending on the sent ID, adds an icon to the imageview. */
    private void setWeatherDataForHomeScreen(Weather weather) {
        int weatherDegreesInt = calculateWeatherInt(Double.valueOf(weather.getDegrees()));
        int weatherMaxDegreesInt = calculateWeatherInt(Double.valueOf(weather.getMaxDegrees()));
        int weatherMinDegreesInt = calculateWeatherInt(Double.valueOf(weather.getMinDegrees()));
        degrees.setText(weatherDegreesInt + CELSIUS);
        maxDegrees.setText(weatherMaxDegreesInt + CELSIUS);
        minDegrees.setText(weatherMinDegreesInt + CELSIUS);
        weatherDescription.setText(weather.getWeatherDescription());
        String latestTime = weatherLastUpdateDataProvider.getLatestUpdateTime();
        lastUpdated.setText(getResources().getString(R.string.last_updated) + " " + latestTime);
        assignWeatherIcon(weather.getweatherIcon(), weather.getSunrise(), weather.getSunset());
    }

    /* Inserts the most recent weather data into the weather database after having deleted the data which has been in it before. */
    private void insertWeatherDataInLastUpdateProvider(Weather weather) {
        weatherLastUpdateDataProvider.deleteLatestUpdate();
        weatherLastUpdateDataProvider.addLatestUpdate(weather);
    }

    /* Cuts off redundant decimal digits from the degree double. Casts the double to an integer. */
    private int calculateWeatherInt(double degreeDouble) {
        return (int)degreeDouble;
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
    private void assignWeatherIcon(String weatherIconID, long sunrise, long sunset) {
        boolean itIsDay = checkIfItIsDayOrNight(sunrise, sunset);
        if (itIsDay == true) {
            assignDayLightWeatherIcons(weatherIconID);
        } else {
            assignNightWeatherIcons(weatherIconID);
        }

    }


    /* This method/algorithm was written using the tutorial being accessible at the following website:
    * http://code.tutsplus.com/tutorials/create-a-weather-app-on-android--cms-21587
    * Depending on which weatherIconID it receives it sets the image of the weatherIcon imageview.
    * That is to say it loads the respective drawable resource.
    * For further information consult the comment on the assignWeatherIcon method.
    * Actually this method acts in the same way like the assignDayLightWeatherIcons. The icons, however, are meant to be night icons.*/
    private void assignNightWeatherIcons(String weatherIconID) {
        int weatherID = Integer.valueOf(weatherIconID);
        int reducedWeatherIconID = (weatherID / WEATHER_DIVISION_CONSTANT);
        if (weatherID == WEATHER_EXTENDED_ID_SUNNY) {
            weatherIcon.setImageResource(R.drawable.ic_weather_night_clear_night);
        } else if (weatherID == WEATHER_EXTENDED_ID_SUNNY_WITH_VERY_FEW_CLOUDS){
            weatherIcon.setImageResource(R.drawable.ic_weather_night_very_few_clouds);
        }
            else {
                switch (reducedWeatherIconID) {
                    case WEATHER_ID_THUNDERSTORM:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_storm);
                        break;
                    case WEATHER_ID_FOG:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_many_clouds);
                        break;
                    case WEATHER_ID_SNOW:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_snow);
                        break;
                    case WEATHER_ID_LIGHT_RAIN:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_a_little_rain);
                        break;
                    case WEATHER_ID_A_LOT_OF_RAIN:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_a_lot_of_rain);
                        break;
                    case WEATHER_ID_SUNNY_AND_CLOUDY:
                        weatherIcon.setImageResource(R.drawable.ic_weather_night_many_clouds);
                }

            }
    }



    /* This method/algorithm was written using the tutorial being accessible at the following website:
    * http://code.tutsplus.com/tutorials/create-a-weather-app-on-android--cms-21587 .
    * Depending on the weatherIconID this method receives as a parameter, the weather icon is set.
    * Contrary to the assignNightWeatherIcons, these are, however, icons that are apt for daylight.
    * Please consult the comments on the assignNightWeatherIcons as well as assignWeatherIcon method for further information. */
    private void assignDayLightWeatherIcons(String weatherIconID) {
        int weatherID = Integer.valueOf(weatherIconID);
        int reducedWeatherIconID = (weatherID / WEATHER_DIVISION_CONSTANT);
        if (weatherID == WEATHER_EXTENDED_ID_SUNNY) {
            weatherIcon.setImageResource(R.drawable.ic_weather_sun_no_clouds);
        } else if (weatherID == WEATHER_EXTENDED_ID_SUNNY_WITH_VERY_FEW_CLOUDS){
            weatherIcon.setImageResource(R.drawable.ic_weather_sunny_very_few_clouds);
        }
        else {
            switch (reducedWeatherIconID) {
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

    /* The idea to multiply sunrise and sunset with TIME_FACTOR = 1000 was taken from
    http://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java .
    This method checks whether the system is used at day or night. */
    private boolean checkIfItIsDayOrNight(long sunrise, long sunset) {
        String sunriseString = formatTimeString(sunrise * TIME_FACTOR);
        String sunsetString = formatTimeString(sunset * TIME_FACTOR);
        long currentTime = System.currentTimeMillis();
        String currentTimeString = formatTimeString(currentTime);
        int sunriseInNumbers = convertToNumbersOnly(sunriseString);
        int sunsetInNumbers = convertToNumbersOnly(sunsetString);
        int currentTimeInNumbers = convertToNumbersOnly(currentTimeString);
        if (sunriseInNumbers <= currentTimeInNumbers && currentTimeInNumbers <= sunsetInNumbers) {
            return true;
        }
            else {
            return false;
        }
    }

    /* Cuts of the redundant information from the time string by creating an integer from an earlier created substring. */
    private int convertToNumbersOnly(String timeString) {
        return Integer.valueOf(timeString.substring(SUBSTRING_START_HOURS,SUBSTRING_END_HOURS)
                + timeString.substring(SUBSTRING_START_MINUTES, SUBSTRING_END_MINUTES));
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

    @Override
    protected void onDestroy () {
        super.onDestroy();
        weatherLastUpdateDataProvider.close();
        furtherInformationDatabase.close();
    }

    @Override
    protected void onResume () {
        super.onResume();
        // following line from: http://stackoverflow.com/questions/12561033/how-to-dynamicaly-change-menu-in-oncreateoptionsmenu
        // This call ensures that after the user has logged out, that is to say, when he or she comes from the MyAccountActivity or else,
        // the right menu resource is loaded.
        this.invalidateOptionsMenu();

    }

    @Override
    public void onFormattedLocationReceived(String formattedLocation) {
        System.out.println(formattedLocation);
    }

}
