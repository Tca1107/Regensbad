package com.example.tom.regensbad.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.parse.RequestPasswordResetCallback;


public class ResetPasswordActivity extends ActionBarActivity {

    /* Constant of the type String that defines the filepath of the "Pacifico" font used for the main heading. */
    private static final String FONT_PACIFICO_FILE_PATH = "Pacifico.ttf";

    /* User interface elements */
    private View marginKeeper;
    private TextView enterMailAddress;
    private EditText mailAddress;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initializeUIElements();
        initializeActionBar();
        registerOnClickListener();
    }


    /* Registers an OnClickListener on the button. Since there is only one of these click listeners needed in this
   * activity, it was decided not to implement the OnClickListener interface but to use the conventional way
   * of registering an OnClickListener on the respective button. */
    private void registerOnClickListener() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /* This method was created using the parse.com documentation, which can be found under
            https://parse.com/docs/android/guide#users as a guideline.
            It send a mail to the given mail address, allowing the user to reset his or her password. */
            public void onClick(View v) {
                String mail = mailAddress.getText().toString();
                if (checkIfConnectedToInternet()) {
                    ParseUser.requestPasswordResetInBackground(mail, new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                showDialog(R.layout.dialog_reset_password_succeeded, R.string.okay, R.string.reset_password_successful_title);
                            } else {
                                showDialog(R.layout.dialog_reset_password_failed, R.string.okay, R.string.reset_password_failed_title);
                            }
                        }
                    }); } else {
                    showDialog(R.layout.dialog_no_internet_connection_reset_password, R.string.okay, R.string.no_internet_connection);
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




    /* This method as well the corresponding layout resource was written using Google Android's developer guide for
    * dialogs as a guideline (http://developer.android.com/guide/topics/ui/dialogs.html#CustomDialog).
    * It shows a dialog that lets the user know that his or her registration failed or succeeded.*/
    private void showDialog(int layoutResource, int messageOnButton, int title){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(layoutResource, null));
        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(messageOnButton, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing, since the dialog only closes
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }


    /* Initializes the user interface elements. */
    private void initializeUIElements () {
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        marginKeeper = findViewById(R.id.view_to_keep_margin);
        enterMailAddress = (TextView)findViewById(R.id.text_view_enter_mail_to_reset_password);
        mailAddress = (EditText)findViewById(R.id.edit_text_mail_address);
        resetButton = (Button)findViewById(R.id.button_reset_password);
    }

    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = ResetPasswordActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ResetPasswordActivity.this.getResources().getColor(R.color.blue_dark_primary_color));
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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
