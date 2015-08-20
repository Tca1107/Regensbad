package com.example.tom.regensbad;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class CreateAccountOrSignInActivity extends ActionBarActivity implements View.OnClickListener{

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Fonts/Pacifico.ttf";

    /* User interface elements */
    private TextView appName;
    private View marginKeeperOne;
    private EditText username;
    private EditText password;
    private Button signIn;
    private View marginKeeperTwo;
    private TextView createNewAccount;
    private Button submitNewAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_or_sign_in);
        initializeUIElements();
        registerOnClickListeners();
        initializeActionBar();
        setFontOfAppName();
    }

    /* Initializes the Action Bar*/
    private void initializeActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // hier noch Methode zum Icon einfügen, sobald wir das Icon haben
    }


    private void registerOnClickListeners() {
        signIn.setOnClickListener(this);
        submitNewAccount.setOnClickListener(this);
    }

    /* Initializes the elements of the user interface. */
    private void initializeUIElements () {
        appName = (TextView)findViewById(R.id.text_view_app_name_in_create_account_or_sign_in_activity);
        marginKeeperOne = findViewById(R.id.view_to_keep_margin_one);
        username = (EditText)findViewById(R.id.edit_text_username);
        password = (EditText)findViewById(R.id.edit_text_password);
        signIn = (Button)findViewById(R.id.button_sign_in);
        marginKeeperTwo = findViewById(R.id.view_to_keep_margin_two);
        createNewAccount = (TextView)findViewById(R.id.text_view_create_an_account);
        submitNewAccount = (Button)findViewById(R.id.button_create_new_account);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_create_account_or_sign_in, menu);
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


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       switch(v.getId()) {
           case R.id.button_sign_in:
               // hier noch das Anmelden auf parse.com ermöglichen!
               break;

           case R.id.button_create_new_account:
               changeToCreateAccountActivity();
               break;

       }
       }


    /* Makes the system change to the CreateAccountActivity. */
    private void changeToCreateAccountActivity() {
        Intent changeToCreateAccountActivity = new Intent (CreateAccountOrSignInActivity.this, CreateAccountActivity.class);
        startActivity(changeToCreateAccountActivity);
    }




}
