package com.example.tom.regensbad;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseObject;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Enable Local Datastore.
        /* This piece of code (the following five lines) was taken from the parse.com quick start guide, which can be found under the following link:
         * https://parse.com/apps/quickstart#parse_data/mobile/android/native/existing .*/
        // code for setting up and testing parse.com
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lrveDDA87qqqf7FfRTjPfOFdZ0DrVLEypfg6dDql", "JYvtqFzgpOHVq9OTn8yKcJdC7xM7eRe3hciBhVh8");
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
