package com.example.tom.regensbad.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class CreateAccountActivity extends ActionBarActivity {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    private View marginKeeper;
    private TextView createNewAccount;
    private EditText mailAddress;
    private EditText username;
    private EditText password;
    private Button submitNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initializeUIElements();
        initializeActionBar();
        registerOnClickListener();
    }


    /* Registers an OnClickListener on the button. Since there is only one of these click listeners needed in this
    * activity, it was decided not to implement the OnClickListener interface but to use the conventional way
    * of registering an OnClickListener on the respective button. */
    private void registerOnClickListener() {
        submitNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfConnectedToInternet()){
                processUserInputForAccountCreation();
                } else {
                    showDialog(R.layout.dialog_no_internet_connection_create_account, R.string.okay, R.string.no_internet_connection);
                }

            }
        });
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



    /* This method was written using the tutorial "How to customize / change ActionBar font, text, color, icon, layout and so on
    with Android", which is available at:
     http://www.javacodegeeks.com/2014/08/how-to-customize-change-actionbar-font-text-color-icon-layout-and-so-on-with-android.html .*/
    private void initializeActionBar() {
            String actionBarTitle = getResources().getString(R.string.app_name);
            getSupportActionBar().setTitle(actionBarTitle);
            this.getSupportActionBar().setDisplayShowCustomEnabled(true);
            this.getSupportActionBar().setDisplayShowTitleEnabled(false);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.home_screen_action_bar, null);
            Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
            ((TextView)view.findViewById(R.id.text_view_action_bar_home_screen)).setTypeface(typeface);
            this.getSupportActionBar().setCustomView(view);
            // hier noch Methode zum Icon einfügen, sobald wir das Icon haben
    }




    /* Initializes the elements of the user interface. */
    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        marginKeeper = findViewById(R.id.view_to_keep_margin);
        createNewAccount = (TextView)findViewById(R.id.text_view_create_an_account);
        mailAddress = (EditText)findViewById(R.id.edit_text_mail_address);
        username = (EditText)findViewById(R.id.edit_text_username);
        password = (EditText)findViewById(R.id.edit_text_password);
        submitNewAccount = (Button)findViewById(R.id.button_submit_new_account);
    }

    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = CreateAccountActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(CreateAccountActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }

    /* This method was created using the parse.com documentation, which can be found under
   https://parse.com/docs/android/guide#users as a guideline.
   This method creates a new user account and saves the information to the backend. */
    private void processUserInputForAccountCreation() {
        String mailAddress = this.mailAddress.getText().toString();
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        // hier noch toasten was genau fehlt, falls was leer is
        ParseUser user = new ParseUser();
        user.setEmail(mailAddress);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showDialog(R.layout.dialog_sign_in_succeeded, R.string.okay, R.string.submisson_succesful_title);
                } else {
                    showDialog(R.layout.dialog_sign_in_failed, R.string.okay, R.string.submisson_failed_title);
                }
            }
        });

    }

    private void showWhatIsMissing(String mailAddress, String username, String password) {
    }

    private void switchToHomeScreenActivityWithALoggedInUser() {
        // just for debugging (following two lines)
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.d("current Uzer", currentUser.toString());
        Intent switchToHomeScreenActivity = new Intent (CreateAccountActivity.this, HomeScreenActivity.class);
        startActivity(switchToHomeScreenActivity);
    }

    /* This method as well the corresponding layout resource was written using Google Android's developer guide for
    * dialogs as a guideline (http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog).
    * It shows a dialog that lets the user know that his or her registration failed or succeeded.*/
    private void showDialog(final int layoutResource, int messageOnButton, int title){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(layoutResource, null));
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(messageOnButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (layoutResource == R.layout.dialog_sign_in_succeeded) {
                    switchToHomeScreenActivityWithALoggedInUser();
                } else {
                    // nothing, since the dialog only closes
                }
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
