package com.example.tom.regensbad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.ParseException;



/* Das ist der Plan:
The following activity will only be shown when the user opens the app and he or she has never used it before or
* has logged out when he or she used it the last time. In all other cases, the app will start with the home screen activity. */

public class LoginActivity extends Activity implements View.OnClickListener{

    /* Constants that are needed to initialize parse.com as the backend of the application. */
    private static final String PARSE_COM_APPLICATION_ID = "lrveDDA87qqqf7FfRTjPfOFdZ0DrVLEypfg6dDql";
    private static final String PARSE_COM_CLIENT_ID = "JYvtqFzgpOHVq9OTn8yKcJdC7xM7eRe3hciBhVh8";

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Fonts/Pacifico.ttf";

    /* User interface elements */
    private ImageView appIcon;
    private TextView appName;
    private TextView registration;
    private Button login;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpParseComAsBackend();
        checkIfUserIsStillLoggedIn();
        initializeUIElements();
        registerOnClickListeners();
    }


    /* This method of code (the following five lines) was taken from the parse.com quick start guide, which can be found under the following link:
    * https://parse.com/apps/quickstart#parse_data/mobile/android/native/existing .
    * This method checks whether the user is still logged in and, if so, makes the system open the HomeScreenActivity immediately.
     * In doing so, it is highly probable that a NullPointer Exception is thrown, which is why the method's body is surrounded by
     * a try and catch block. */
    private void checkIfUserIsStillLoggedIn() {
        try {
            ParseUser currentUser = ParseUser.getCurrentUser();
            Log.d("currentUser", currentUser.toString());
            if (currentUser != null) {
                Log.d("currentUser", currentUser.toString());
                switchToHomeScreenActivity();
            }
        } catch (NullPointerException e) {

        }
    }

    private void registerOnClickListeners() {
        login.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    /* Initializes the elements of the user interface. */
    private void initializeUIElements() {
        appIcon = (ImageView)findViewById(R.id.image_view_app_icon);
        setIconOfImageView();
        appName = (TextView)findViewById(R.id.text_view_app_name);
        setFontOfAppName();
        registration = (TextView)findViewById(R.id.text_view_login);
        login = (Button)findViewById(R.id.button_login);
        skip = (Button)findViewById(R.id.button_skip);
    }


    /* This method was created using the tutorial on including external fonts in Android Studio which can be found
    * at the following website: http://www.thedevline.com/2014/03/how-to-include-fonts-in-android.html .
    * The font used is a font of Google Fonts named "Pacifico", which can be found at the following website:
    * https://www.google.com/fonts/ .*/
    private void setFontOfAppName() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        appName.setTypeface(typeface);

    }


    /* Provides the image view with the application's icon*/
    private void setIconOfImageView() {
        //appIcon.setImageResource(R.drawable.placeholder_icon_for_login_activity);
    }


    /* This piece of code (the following five lines) was taken from the parse.com quick start guide, which can be found under the following link:
    * https://parse.com/apps/quickstart#parse_data/mobile/android/native/existing .
    * This method sets up parse.com as the backend of the application. */
    private void setUpParseComAsBackend() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_COM_APPLICATION_ID, PARSE_COM_CLIENT_ID);
    }

    /* Processes the user's clicks on the buttons. */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                switchToCreateAccountOrSignInActivity();
                break;

            case R.id.button_skip:
                switchToHomeScreenActivity();
                break;
        }

    }

    /* Method that makes the system switch into the HomeScreenActivity. */
    private void switchToHomeScreenActivity() {
        Intent switchToHomeScreenActivity = new Intent (LoginActivity.this, HomeScreenActivity.class);
        startActivity(switchToHomeScreenActivity);
    }

    /* Method that makes the system switch into the CreateAccountOrSignInActivity. */
    private void switchToCreateAccountOrSignInActivity() {
        Intent switchToCreateAccountOrSignInActivity = new Intent(LoginActivity.this, CreateAccountOrSignInActivity.class);
        startActivity(switchToCreateAccountOrSignInActivity);
    }


    /*
    Methods that would only be necessary, if there was an action bar.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
    */
}
