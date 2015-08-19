package com.example.tom.regensbad;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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



    /* User interface elements */
    private TextView createAccount;
    private EditText username;
    private EditText password;
    private Button submitAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_or_sign_in);
        initializeUIElements();
        registerOnClickListeners();
        initializeActionBar();
    }

    /* Initializes the Action Bar*/
    private void initializeActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // hier noch Methode zum Icon einfügen, sobald wir das Icon haben
    }


    private void registerOnClickListeners() {
        submitAccount.setOnClickListener(this);
    }

    /* Initializes the elements of the user interface. */
    private void initializeUIElements () {
        createAccount = (TextView)findViewById(R.id.text_view_create_an_account);
        username = (EditText)findViewById(R.id.edit_text_username);
        password = (EditText)findViewById(R.id.edit_text_password);
        submitAccount = (Button)findViewById(R.id.button_sign_in);
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
               processUserInputForAccountCreation();

       }
       }

    /* This method was created using the parse.com documentation, which can be found under
    https://parse.com/docs/android/guide#users as a guideline.
    This method creates a new user account and saves the information to the backend. */
    private void processUserInputForAccountCreation() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        ParseUser user = new ParseUser();
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



}
