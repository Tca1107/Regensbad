package com.example.tom.regensbad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        getMenuInflater().inflate(R.menu.menu_create_account_or_sign_in, menu);
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
                    showSuccessfulDialog();
                } else {
                    showFailToast();
                }
            }
        });

    }

    /* This method as well the corresponding layout resource was written using Google Android's developer guide for
    * dialogs as a guideline (http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog).
    * It shows a dialog that lets the user know that his or her registration was successful.*/
    private void showSuccessfulDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(R.layout.dialog_sign_in_succeeded, null));
        dialogBuilder.setPositiveButton(R.string.okay, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing, since the dialog only closes
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showFailToast() {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(CreateAccountOrSignInActivity.this, R.string.submission_failed, duration);
        toast.show();
    }

}
