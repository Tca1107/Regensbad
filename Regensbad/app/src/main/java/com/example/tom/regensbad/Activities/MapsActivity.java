package com.example.tom.regensbad.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.tom.regensbad.Domain.CivicPool;
import com.example.tom.regensbad.Persistence.Database;
import com.example.tom.regensbad.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Marker singleMarker;

    private String origin;
    private int ID;

    private CivicPool singlePool;

    private Database db;

    private static final double START_LAT = 49.012985;
    private static final double START_LANG = 12.092370;
    private static final float START_ZOOM = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDB();
        getExtras();
        initializeUIElements();
        setUpMapIfNeeded();
    }

    protected void onDestroy(){
        db.close();
        super.onDestroy();
    }

    private void initDB() {
        db = new Database(this);
        db.open();
    }

    private void getExtras() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        origin = extras.getString("origin");
        if (origin.equals("detail")){
            ID = extras.getInt("ID");
            singlePool = db.getPoolItem(ID);
        }
    }

    private void setSingleStartPosition() {
        /*From: http://stackoverflow.com/questions/14074129/google-maps-v2-set-both-my-location-and-zoom-in*/
        CameraUpdate start = CameraUpdateFactory.newLatLng(new LatLng(singlePool.getLati(), singlePool.getLongi()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(START_ZOOM);

        mMap.moveCamera(start);
        mMap.animateCamera(zoom);
    }

    private void initializeUIElements() {
        setContentView(R.layout.activity_maps);
        //From: http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
        if(Integer.parseInt(android.os.Build.VERSION.SDK)>=21){
            setStatusBarColor();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        //From: http://stackoverflow.com/questions/27093287/how-to-change-status-bar-color-to-match-app-in-lollipop-android
        Window window = MapsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(MapsActivity.this.getResources().getColor(R.color.blue_dark_primary_color));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        handleClick();
        if(origin.equals("detail")){
            setSingleStartPosition();
            singleMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(singlePool.getLati(), singlePool.getLongi())).title(singlePool.getName()).snippet("Details"));
        }
  }

    private void handleClick() {
        /*From: http://stackoverflow.com/questions/14226453/google-maps-api-v2-how-to-make-markers-clickable*/
        mMap.setOnInfoWindowClickListener(this);
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.equals(singleMarker)){
            Toast.makeText(MapsActivity.this, "Es hat funktioniert!", Toast.LENGTH_SHORT).show();
            Intent showDetailView = new Intent(MapsActivity.this, CivicPoolDetailActivity.class);
            showDetailView.putExtra("ID", singlePool.getID());
            startActivity(showDetailView);
        }
    }


}
