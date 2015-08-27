package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tom.regensbad.Adapters.ListAdapter;
import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.R;

import java.sql.SQLException;
import java.util.ArrayList;


public class AllCivicPoolsActivity extends ActionBarActivity {

    private ListView list;
    private ListAdapter adapter;
    private ArrayList<CivicPool> pools = new ArrayList<CivicPool>();

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB();
        initializeUIElements();
        initPoolList();
        handleClick();
    }

    private void handleClick() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CivicPool pool = (CivicPool) adapterView.getItemAtPosition(i);

                Intent showDetailView = new Intent(AllCivicPoolsActivity.this, CivicPoolDetailActivity.class);
                showDetailView.putExtra("name", pool.getName());
                showDetailView.putExtra("type", pool.getType());
                showDetailView.putExtra("lati", pool.getLati());
                showDetailView.putExtra("longi", pool.getLongi());
                showDetailView.putExtra("number", pool.getPhoneNumber());
                showDetailView.putExtra("website", pool.getWebsite());
                showDetailView.putExtra("openTime", pool.getOpenTime());
                showDetailView.putExtra("closeTime", pool.getCloseTime());
                showDetailView.putExtra("imgPath", pool.getPicPath());

                System.out.println(pool.getOpenTime() + "");
                System.out.println(pool.getCloseTime() + "");

                startActivity(showDetailView);
            }
        });
    }

    private void initPoolList() {
        updateList();
    }

    private void updateList() {
        pools.clear();
        pools.addAll(db.getAllPoolItems());
        adapter.notifyDataSetChanged();
    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

    private void initDB() {
        db = new Database(this);
        db.open();
        db.saveCivicPoolsintoDB();
    }

    private void initializeUIElements() {
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
        setContentView(R.layout.activity_list_view);
        list = (ListView) findViewById(R.id.listview_lake_list);
        initAdapter();
    }

    private void initAdapter() {
        adapter = new ListAdapter(this, pools);
        list.setAdapter(adapter);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = AllCivicPoolsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(AllCivicPoolsActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_civic_pools, menu);
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
