package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.R;


public class CivicPoolDetailActivity extends ActionBarActivity {

    private int ID;

    private ImageView poolPicture;
    private TextView textName;
    private TextView textOpenTime;
    private TextView textPhoneNumber;
    private TextView textWebsite;

    private Button showMapButton;

    private Database db;

    private CivicPool pool;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();
        getExtras();
        initializeUIElements();
        handleInput();

    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

    private void initDatabase() {
        db = new Database(this);
        db.open();
    }

    private void handleInput() {
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent(CivicPoolDetailActivity.this, MapsActivity.class);
                goToMap.putExtra("ID", pool.getID());
                goToMap.putExtra("origin", "detail");
                startActivity(goToMap);
            }
        });

        textWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From: http://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
                Intent startBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(pool.getWebsite()));
                startActivity(startBrowser);
            }
        });

        textPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From: http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically
                Intent makeCall = new Intent(Intent.ACTION_CALL);
                makeCall.setData(Uri.parse("tel:"+pool.getPhoneNumber()));
                startActivity(makeCall);
            }
        });
    }

    private void getExtras() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        ID = extras.getInt("ID");
        pool = db.getPoolItem(ID);

    }

    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_detail_view);
        poolPicture = (ImageView)findViewById(R.id.imageView_bathIMG);
        textName = (TextView) findViewById(R.id.textView_bathName);
        textOpenTime = (TextView) findViewById(R.id.textview_openTime);
        textPhoneNumber = (TextView) findViewById(R.id.text_phoneNumber);
        textWebsite = (TextView) findViewById(R.id.text_website);

        textName.setText(pool.getName());
        textPhoneNumber.setText(pool.getPhoneNumber());


        /*Following four lines retrieved from: http://stackoverflow.com/questions/13105430/android-setting-image-from-string */
        String picPath = db.getPicPath(ID);
        int id = getResources().getIdentifier(picPath, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        //drawable.setBounds(100,100,100,100);
        poolPicture.setImageDrawable(drawable);
        poolPicture.setScaleType(ImageView.ScaleType.FIT_XY);






        createTimeView();

        showMapButton = (Button) findViewById(R.id.button_showOnMap);
    }

    private void createTimeView() {
        String timeString = " " + pool.getOpenTime().substring(0,2) + ":" + pool.getOpenTime().substring(2) + " - " + pool.getCloseTime().substring(0, 2) + ":" + pool.getCloseTime().substring(2);
        textOpenTime.setText(timeString);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = CivicPoolDetailActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(CivicPoolDetailActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_civic_pool_detail, menu);
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
}
