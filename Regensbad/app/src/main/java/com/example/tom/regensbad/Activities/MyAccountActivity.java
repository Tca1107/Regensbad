package com.example.tom.regensbad.Activities;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.regensbad.R;
import com.parse.ParseUser;

public class MyAccountActivity extends ActionBarActivity implements View.OnClickListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    private TextView myAccount;
    private TextView userName;
    private TextView mailAddress;
    private Button changePassword;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initializeUIElements();
        registerOnClickListeners();
        setValuesToTextViews();
        initializeActionBar();
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

    private void setValuesToTextViews() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String username = currentUser.getUsername();
        String mailAddress = currentUser.getEmail();
        setTheseValuesToTheTextViews(username, mailAddress);
    }

    private void setTheseValuesToTheTextViews(String username, String mailAddress) {
        userName.setText(getResources().getString(R.string.user_name) + " " + username);
        this.mailAddress.setText(getResources().getString(R.string.mail_address_in_my_account_activity) + " " + mailAddress);
    }

    private void registerOnClickListeners() {
        changePassword.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void initializeUIElements() {
        setContentView(R.layout.activity_my_account);
        myAccount = (TextView)findViewById(R.id.text_view_my_account);
        userName = (TextView)findViewById(R.id.text_view_user_name);
        mailAddress = (TextView)findViewById(R.id.text_view_mail_address);
        changePassword = (Button)findViewById(R.id.button_change_password);
        logout = (Button)findViewById(R.id.button_logout);
    }




    @Override
    /* Depending on whether a user is signed in and on whether the system is connected to the internet,*/
    /* this method loads the respective menu resource file.*/
    /* This method was created using the following to resources as guidelines:*/
    /* http://developer.android.com/guide/topics/ui/actionbar.html#ActionView */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && checkIfConnectedToInternet() == true) {
            getMenuInflater().inflate(R.menu.menu_user_online_with_neither_logout_nor_my_account, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_my_account, menu);
            return super.onCreateOptionsMenu(menu);
        }
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_change_password:
                changeToResetPasswordActivity();
                break;
            case R.id.button_logout:
                logoutAndGoBack();
                break;
        }
    }

    private void logoutAndGoBack() {
        // from https://parse.com/docs/android/guide#users
        ParseUser.logOut();
        finish();
    }




    private void changeToResetPasswordActivity() {
        Intent changeToResetPasswordActivity = new Intent (MyAccountActivity.this, ResetPasswordActivity.class);
        startActivity(changeToResetPasswordActivity);
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




}
