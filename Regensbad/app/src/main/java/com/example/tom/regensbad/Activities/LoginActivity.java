package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tom.regensbad.R;
import com.parse.Parse;
import com.parse.ParseUser;



/* Das ist der Plan:
The following activity will only be shown when the user opens the app and he or she has never used it before or
* has logged out when he or she used it the last time. In all other cases, the app will start with the home screen activity. */

public class LoginActivity extends Activity implements View.OnClickListener{

    /* Constants that are needed to initialize parse.com as the backend of the application. */
    private static final String PARSE_COM_APPLICATION_ID = "lrveDDA87qqqf7FfRTjPfOFdZ0DrVLEypfg6dDql";
    private static final String PARSE_COM_CLIENT_ID = "JYvtqFzgpOHVq9OTn8yKcJdC7xM7eRe3hciBhVh8";

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

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
            if (currentUser != null && checkIfConnectedToInternet() == true) {
                Log.d("currentUser", currentUser.toString());
                switchToHomeScreenActivity();
            }
        } catch (NullPointerException e) {

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


    private void registerOnClickListeners() {
        login.setOnClickListener(this);
        skip.setOnClickListener(this);
    }

    /* Initializes the elements of the user interface. */
    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        appIcon = (ImageView)findViewById(R.id.ic_regensbad_logo_login);
        appIcon.setImageResource(R.drawable.ic_regensbad_logo_login);
        appName = (TextView)findViewById(R.id.text_view_app_name);
        setFontOfAppName();
        registration = (TextView)findViewById(R.id.text_view_login);
        login = (Button)findViewById(R.id.button_login);
        skip = (Button)findViewById(R.id.button_skip);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = LoginActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(LoginActivity.this.getResources().getColor(R.color.blue_dark_primary_color));
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
        appIcon.setImageResource(R.drawable.ic_regensbad_logo);
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
                showDialog();
                break;
        }

    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder (this);
        dialogBuilder.setTitle(getResources().getString(R.string.button_skip));
        dialogBuilder.setMessage(getResources().getString(R.string.skip_login));
        dialogBuilder.setPositiveButton((getResources().getString(R.string.continue_anyway)), new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchToHomeScreenActivity();
            }
        });
        dialogBuilder.setNegativeButton((getResources().getString(R.string.cancel)), new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing since the dialog only closes.
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
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
}
