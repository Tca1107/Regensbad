package com.example.tom.regensbad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class CreateAccountActivity extends ActionBarActivity {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Fonts/Pacifico.ttf";

    private TextView appName;
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
        setFontOfAppName();
        registerOnClickListener();
    }


    /* Registers an OnClickListener on the button. Since there is only one of these click listeners needed in this
    * activity, it was decided not to implement the OnClickListener interface but to use the conventional way
    * of registering an OnClickListener on the respective button. */
    private void registerOnClickListener() {
        submitNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUserInputForAccountCreation();
            }
        });
    }

    /* Initializes the Action Bar. */
    private void initializeActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String actionBarTitle = getResources().getString(R.string.create_account_activity_name);
        getSupportActionBar().setTitle(actionBarTitle);
        // hier noch Methode zum Icon einfügen, sobald wir das Icon haben
    }

    /* This method was created using the tutorial on including external fonts in Android Studio which can be found
    * at the following website: http://www.thedevline.com/2014/03/how-to-include-fonts-in-android.html .
    * The font used is a font of Google Fonts named "Pacifico", which can be found at the following website:
    * https://www.google.com/fonts/ .*/
    private void setFontOfAppName() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PACIFICO_FILE_PATH);
        appName.setTypeface(typeface);
    }


    /* Initializes the elements of the user interface. */
    private void initializeUIElements() {
        appName = (TextView)findViewById(R.id.text_view_app_name_in_create_account_activity);
        marginKeeper = findViewById(R.id.view_to_keep_margin);
        createNewAccount = (TextView)findViewById(R.id.text_view_create_an_account);
        mailAddress = (EditText)findViewById(R.id.edit_text_mail_address);
        username = (EditText)findViewById(R.id.edit_text_username);
        password = (EditText)findViewById(R.id.edit_text_password);
        submitNewAccount = (Button)findViewById(R.id.button_submit_new_account);
    }

    /* This method was created using the parse.com documentation, which can be found under
   https://parse.com/docs/android/guide#users as a guideline.
   This method creates a new user account and saves the information to the backend. */
    private void processUserInputForAccountCreation() {
        String mailAddress = this.mailAddress.getText().toString();
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        ParseUser user = new ParseUser();
        user.setEmail(mailAddress);
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showDialog(R.layout.dialog_sign_in_succeeded, R.string.okay);
                    // switchToTheNextActivityWithALoggedInUser
                } else {
                    showDialog(R.layout.dialog_sign_in_failed, R.string.okay);
                }
            }
        });

    }

    /* This method as well the corresponding layout resource was written using Google Android's developer guide for
    * dialogs as a guideline (http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog).
    * It shows a dialog that lets the user know that his or her registration failed or succeeded.*/
    private void showDialog(int layoutResource, int messageOnButton){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(layoutResource, null));
        dialogBuilder.setPositiveButton(messageOnButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing, since the dialog only closes
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
}
