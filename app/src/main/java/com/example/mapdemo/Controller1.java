package com.example.mapdemo;


import com.example.mapdemo.GeolocationService;
import com.example.mapdemo.Model;
import com.example.mapdemo.PermissionUtils;
import com.example.mapdemo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;

/**
 * Created by DC0708 on 31-Jan-16.
 */
public class Controller1 extends AppCompatActivity implements LocationListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, OnRequestPermissionsResultCallback {

    private LatLng InitialLoc;
    GoogleMap googleMap;

    private CheckBox mMyLocationCheckbox;

    public int totalscore=0;

    GeolocationService gps;

    protected LocationListener locationListener;
    protected LocationManager locationManager;

    public Model gamemodel;
    TextView score;

    public Location location;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public String BoundaryType;

    public String bestProvider = LocationManager.GPS_PROVIDER;
    public List<SnackBubble> snacks = new ArrayList<SnackBubble>(20);

    public List<JunkBubble> junks = new ArrayList<JunkBubble>(20);

    public List<WormHole> holes = new ArrayList<WormHole>(20);

    public RepellerBubble repeller;
    public Boolean isGPSEnabled = false;

    public Boolean isNetworkEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        gps = new GeolocationService(Controller1.this);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
            BoundaryType = extras.getString("Boundary");

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMap = mapFragment.getMap();

        updateMapType();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Getting GPS status
        if(locationManager!=null){


            isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(!isGPSEnabled){
                Log.d("in GPS settings","show settings");
                gps.showSettingsAlert();
            }

            bestProvider = locationManager.getBestProvider(criteria, true);

            //Log.d("hererer",location+" ");
            location = locationManager.getLastKnownLocation(bestProvider);

            if (location==null){
                location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                bestProvider=locationManager.GPS_PROVIDER;
            }

            while(location==null){
                location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                bestProvider=locationManager.NETWORK_PROVIDER;
            }

            locationManager.requestLocationUpdates(bestProvider, 33, 0, this);
            onLocationChanged(location);
            FetchGps();

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        else{

            gps.showSettingsAlert();
        }

        Log.d("Here","asdsa");
        Log.d("Here", "asdsa");

    }

    @Override
    public void onMapReady(GoogleMap map) {

        score =(TextView) findViewById(R.id.description);

        score.setText("SCORE :" + String.valueOf(totalscore));

        gamemodel = new Model(BoundaryType);

        if(isGPSEnabled){

            createboundary(BoundaryType);
        }
        else{
            gps.showSettingsAlert();
        }

        createsnackbubbles();
        createjunkbubbles();
        createwormholes();
        createrepeller();
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    public void createsnackbubbles(){

        for(int i=0;i<gamemodel.sizeSnackBubble;i++){
            SnackBubble tempsnack = new SnackBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc);
            snacks.add(tempsnack);
            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(tempsnack.center.x,tempsnack.center.y))
                    .radius(tempsnack.radius)
                    .strokeWidth(tempsnack.mWidth)
                    .strokeColor(tempsnack.mStrokeColor)
                    .fillColor(tempsnack.mFillColor));
        }
    }

    public void createjunkbubbles(){

        for(int i=0;i<gamemodel.sizeJunkBubbles;i++){
            JunkBubble tempjunk = new JunkBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc);
            junks.add(tempjunk);
            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(tempjunk.center.x,tempjunk.center.y))
                    .radius(tempjunk.radius)
                    .strokeWidth(tempjunk.mWidth)
                    .strokeColor(tempjunk.mStrokeColor)
                    .fillColor(tempjunk.mFillColor));;

        }
    }


    public void createwormholes(){

        for(int i=0;i<gamemodel.sizeWormHoles;i++){
            WormHole temphole = new WormHole(gamemodel.xCoordinate,gamemodel.yCoordinate,i,gamemodel,InitialLoc);
            holes.add(temphole);
            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(temphole.center.x,temphole.center.y))
                    .radius(temphole.radius)
                    .strokeWidth(temphole.mWidth)
                    .strokeColor(temphole.mStrokeColor)
                    .fillColor(temphole.mFillColor));;

        }
    }


    public void createrepeller(){

        repeller = new RepellerBubble(gamemodel.isPlacedBubble,gamemodel,InitialLoc);
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(repeller.center.x, repeller.center.y))
                .radius(repeller.radius)
                .strokeWidth(repeller.mWidth)
                .strokeColor(repeller.mStrokeColor)
                .fillColor(repeller.mFillColor));;
    }

    public void createboundary(String boundaryType){

        if (BoundaryType.equals("Large")) {

            Log.d("boundary is", "Large" + BoundaryType);


            if(InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

            } else if (BoundaryType.equals("Medium")) {

            Log.d("boundary is", "medium" + BoundaryType);

            if (InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

        } else {

            Log.d("boundary is", "small" + BoundaryType);

            if (InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

        }


            googleMap.addPolygon(new PolygonOptions()
                    .addAll(createRectangle(new LatLng(InitialLoc.latitude, InitialLoc.longitude), gamemodel.boundaryWidth / 2, gamemodel.boundaryHeight / 2))
                    .strokeColor(Color.BLUE)
                    .strokeWidth(2));
          //  UpdateBubbles(InitialLoc.latitude, InitialLoc.longitude, BoundaryType);

   }

    public List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {

        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist =  (earthRadius * c);

        return dist;
    }

    public void updateMapType(){

        googleMap.setMapType(2);

    }

    private boolean checkReady() {
        if (googleMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void FetchGps(){


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if(isGPSEnabled) {

//            String bestProvider = locationManager.getBestProvider(criteria, true);
//            location = locationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                InitialLoc = new LatLng(location.getLatitude(), location.getLongitude());

            } else {
                Log.d("location error", "location is not enabled!!");
            }
            if (location != null) {
                onLocationChanged(location);
                Log.d(" initial location set ", location.getLatitude() + " " + location.getLongitude());
                Log.d("Location is if changed ", location.getLatitude() + " " + location.getLongitude());
            }

        }
        else{

            gps.showSettingsAlert();
        }

    }

    private void updateMyLocation() {
        if(!checkReady()) {
            return;
        }

        if(!mMyLocationCheckbox.isChecked()) {
            googleMap.setMyLocationEnabled(false);
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            mMyLocationCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

}
