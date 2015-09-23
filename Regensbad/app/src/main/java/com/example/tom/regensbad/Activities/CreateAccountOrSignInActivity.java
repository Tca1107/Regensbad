package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tom.regensbad.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class CreateAccountOrSignInActivity extends ActionBarActivity implements View.OnClickListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    /* User interface elements */
    private View marginKeeperOne;
    private EditText username;
    private EditText password;
    private Button signIn;
    private TextView forgotPassword;
    private TextView createNewAccount;
    private Button submitNewAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_or_sign_in);
        initializeUIElements();
        registerOnClickListeners();
        initializeActionBar();
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

    /* Registers OnClickListeners. */
    private void registerOnClickListeners() {
        signIn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        submitNewAccount.setOnClickListener(this);
    }

    /* Initializes the elements of the user interface. */
    private void initializeUIElements () {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        marginKeeperOne = findViewById(R.id.view_to_keep_margin_one);
        username = (EditText)findViewById(R.id.edit_text_username);
        password = (EditText)findViewById(R.id.edit_text_password);
        signIn = (Button)findViewById(R.id.button_sign_in);
        forgotPassword = (TextView)findViewById(R.id.text_view_forgot_password);
        createNewAccount = (TextView)findViewById(R.id.text_view_create_an_account);
        submitNewAccount = (Button)findViewById(R.id.button_create_new_account);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = CreateAccountOrSignInActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(CreateAccountOrSignInActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Handles the clicks and shows a respective dialog, if the system is not connected to the internet. */
    @Override
    public void onClick(View v) {
       switch(v.getId()) {
           case R.id.button_sign_in:
               if (checkIfConnectedToInternet()) {
                   signInWithUserAccount();
               } else {
                   showDialog(R.layout.dialog_no_internet_connection_sign_in, R.string.okay, R.string.no_internet_connection);
               }
               break;
           case R.id.text_view_forgot_password:
               changeToResetPasswordActivity();
               break;
           case R.id.button_create_new_account:
               changeToCreateAccountActivity();
               break;
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

    private void changeToResetPasswordActivity() {
        Intent changeToResetPasswordActivity = new Intent (CreateAccountOrSignInActivity.this, ResetPasswordActivity.class);
        startActivity(changeToResetPasswordActivity);
    }

    /* This method was created using the parse.com documentation, which can be found under
   https://parse.com/docs/android/guide#users as a guideline.
   It allows the user to login with his or her account. */
    private void signInWithUserAccount() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    changeToHomeScreenActivity();
                } else {
                    showDialog(R.layout.dialog_check_in_failed, R.string.okay, R.string.sign_in_failed_title);
                }
            }
        });

    }

    /* This method as well the corresponding layout resource was written using Google Android's developer guide for
    * dialogs as a guideline (http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog).
    * It shows a dialog that lets the user know that his or her registration failed.*/
    private void showDialog(int layoutResource, int messageOnButton, int title){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(layoutResource, null));
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(messageOnButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing since the dialog only closes.
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /* Method that makes the system switch into the HomeScreenActivity. */
    private void changeToHomeScreenActivity() {
        Intent switchToHomeScreenActivity = new Intent (CreateAccountOrSignInActivity.this, HomeScreenActivity.class);
        startActivity(switchToHomeScreenActivity);
    }


    /* Makes the system change to the CreateAccountActivity. */
    private void changeToCreateAccountActivity() {
        Intent changeToCreateAccountActivity = new Intent (CreateAccountOrSignInActivity.this, CreateAccountActivity.class);
        startActivity(changeToCreateAccountActivity);
    }

}
