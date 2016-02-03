/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.analytics.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.example.mapdemo.PlayerBubble;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DC
 */


public class TopView extends AppCompatActivity implements LocationListener,SeekBar.OnSeekBarChangeListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleMap googleMap;
    private static LatLng gpsLocation;
    private LatLng InitialLoc;

//    private static final LatLng INDIA = new LatLng(-33.86365, 151.20589);
//
//    private static final LatLng PAK = new LatLng(-33.88365, 151.20389);
//
//    private static final LatLng ENG = new LatLng(-33.87365, 151.21689);
//
//    private static final LatLng DC = new LatLng(-33.86165, 151.21892);

    private List<DraggableCircle> circles = new ArrayList<DraggableCircle>(10);

    private static final double DEFAULT_RADIUS = 1;

    private static int timeplayed = 0;

    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    private static final int WIDTH_MAX = 50;

    private static final int HUE_MAX = 360;

    private int numberOfBubbles;

    private Boolean gameover=false;

    private int warmHoleCount;

    private int Lastcollisionboundary=-1;

    private static final int ALPHA_MAX = 255;

    private GoogleMap mMap;

    public int checker =1;

    String BoundaryType;

    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);

    private List<DraggableCircle> bCircles = new ArrayList<DraggableCircle>(5);

    private List<DraggableCircle> rCircles = new ArrayList<DraggableCircle>(5);

    double width,height;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    private SeekBar mColorBar;
    private TextView score;
    private SeekBar mAlphaBar;

    private SeekBar mWidthBar;

    private int mStrokeColor;

    private int mFillColor;

    private int mWidth;

    private int totalscore=0;

    private double maxBubbleSize = 2.5;

    private double playerRadius;

    double bubbleSpeed = 1.0/250000.0;

    double speedx = -1.0;

    double speedy = -1.0;

    public BubblePhysics bphy =new BubblePhysics();

    private class DraggableCircle {

        //private final Marker centerMarker;

        //private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        private int direction;

        private int bTag;

        private double speed;

        public DraggableCircle(LatLng center, double radius, int direction, int bTag, double speed) {
            this.radius = radius;
            this.direction = direction;
            this.bTag = bTag;
            this.speed = speed;
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(mWidth)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng,int direction, int bTag, double speed) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            this.direction = direction;
            this.bTag = bTag;
            this.speed = speed;
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(mWidth)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }


        public void onStyleChange() {
            circle.setStrokeWidth(mWidth);
            circle.setFillColor(mFillColor);
            circle.setStrokeColor(mStrokeColor);
        }
    }


    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

//small --> 0.00025 , 0.00025
//medium --> 0.00035 , 0.00035
//large --> 0.00050 , 0.00050

    GeolocationService gps;

    private CheckBox mMyLocationCheckbox;

    protected LocationListener locationListener;
    protected LocationManager locationManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);
        gps = new GeolocationService(TopView.this);

        score =(TextView) findViewById(R.id.description);

        score.setText("  SCORE :" + String.valueOf(totalscore));
        Bundle extras = getIntent().getExtras();
        //getIntent().
        if(extras != null)
            BoundaryType = extras.getString("Boundary");

       // mMyLocationCheckbox = (CheckBox) findViewById(R.id.my_location);

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(false);

        mMap = googleMap;
        updateMapType();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // Getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if(gps.canGetLocation()) {
            String bestProvider = locationManager.getBestProvider(criteria, true);
            final Location location = locationManager.getLastKnownLocation(bestProvider);

            if(location != null){
                InitialLoc = new LatLng(location.getLatitude(), location.getLongitude());

            }

            else{
                Log.d("location error","location is not enabled!!");
            }
            if (location != null) {
                onLocationChanged(location);
                Log.d(" initial location set ", location.getLatitude() + " " + location.getLongitude());
                Log.d("Location is if changed ", location.getLatitude() + " " + location.getLongitude());
            }
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            //   Log.d("size ",String.valueOf(circles.size()));


                            /*******Collission with Boundary ************/
                            OnCollisionBoundary(circles,InitialLoc,height,width);

                            CollisionNonPlayerBubbles(circles,mMap);

                            CollisionPlayerNonplayer(circles,mCircles,bCircles);

                            RepellerPhysics();

                            if(gameover==true){

                                timer.cancel();
                                timer.purge();
                            }

                            if(circles.size()>0){
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        TopView.this).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("CONGRATTSSS!!!");
                                Log.d("Game is "," Over !!!");
                                // Setting Dialog Message
                                alertDialog.setMessage("You Won. You are a champ, but are you pro??");


                                alertDialog.setButton("START AGAIN", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to execute after dialog closed
                                        getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(new Intent(TopView.this, MainActivity.class));
                                        Toast.makeText(getApplicationContext(), "You clicked on RESTART", Toast.LENGTH_SHORT).show();
                                        timer.cancel();
                                        timer.purge();
                                        finish();
                                    }
                                });
                            }

                            else if(circles.size()>0)
                                OnPlayerBoundaryCollission(new LatLng(location.getLatitude(),location.getLongitude()),height,width);
                            else if(circles.size()<=0){
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        TopView.this).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("OOOPSSS!!!");
                                Log.d("Game is "," Over !!!");
                                // Setting Dialog Message
                                alertDialog.setMessage("You scored " + String.valueOf(totalscore) + " .Game Over!!");


                                alertDialog.setButton("START AGAIN", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to execute after dialog closed
                                        getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(new Intent(TopView.this, MainActivity.class));
                                        timer.cancel();
                                        Toast.makeText(getApplicationContext(), "You clicked on RESTART", Toast.LENGTH_SHORT).show();
                                        timer.purge();

                                        finish();
                                    }
                                });
                            }

                            timeplayed+=1;

                            /*******  -------  ************ /
                             int check=0;
                             /****** ----- Collision of non player bubbles ***
                             for(int i=0;i<circles.size();i++){

                             for(int j=0;j<circles.size();j++){

                             if(i!=j){
                             double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, circles.get(j).circle.getCenter().latitude, circles.get(j).circle.getCenter().longitude);

                             if(circles.get(i).radius+circles.get(j).radius>=Math.abs(distanceBwBubbles)){


                             double r1 = circles.get(i).radius;
                             double r2 = circles.get(j).radius;

                             double lat = r1>=r2?circles.get(i).circle.getCenter().latitude:circles.get(j).circle.getCenter().latitude;
                             double lon = r1>=r2?circles.get(i).circle.getCenter().longitude:circles.get(j).circle.getCenter().longitude;
                             int dir = r1>=r2?circles.get(i).direction:circles.get(j).direction;
                             int tag = r1>=r2?circles.get(i).bTag:circles.get(j).bTag;
                             double sp = r1>=r2?circles.get(i).speed:circles.get(j).speed;

                             double newRadius;
                             if (circles.get(i).bTag==circles.get(j).bTag)
                             newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                             else
                             newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                             //    Log.d("New Radius is"," "+newRadius);

                             //    Log.d("size of circles before removal", " "+ circles.size());
                             //circles.get(i).
                             if(i<j)
                             j-=1;
                             circles.get(i).circle.remove();

                             circles.remove(i);

                             circles.get(j).circle.remove();


                             circles.remove(j);
                             //  Log.d("size of circles", " "+ circles.size());
                             if (tag==1)
                             mFillColor = Color.YELLOW;
                             else
                             mFillColor = Color.GREEN;
                             circles.add(new DraggableCircle(new LatLng(lat,lon), newRadius,dir,tag,sp));
                             check=1;
                             break;
                             }


                             }


                             }

                             if(check==1)
                             break;



                             }

                             /****** ----- Collision of PLayer bubble ***
                             if(mCircles.size()>0){
                             DraggableCircle player = mCircles.get(mCircles.size()-1);

                             for(int i =0 ; i <circles.size();i++){

                             double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, mCircles.get(mCircles.size()-1).circle.getCenter().latitude, mCircles.get(mCircles.size()-1).circle.getCenter().longitude);

                             if(circles.get(i).radius+mCircles.get(mCircles.size()-1).radius>=Math.abs(distanceBwBubbles)) {
                             Log.d("collision with player", " " + circles.get(i).bTag);
                             if (player.radius < circles.get(i).radius) {
                             int size = mCircles.size();
                             mCircles.get(size - 1).circle.remove();
                             mCircles.remove(size - 1);
                             break;
                             } else {
                             if (circles.get(i).bTag == 1) {
                             double r1 = player.radius;
                             double r2 = circles.get(i).radius;

                             double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);
                             player.circle.setRadius(newRadius);
                             circles.get(i).circle.remove();
                             circles.remove(i);
                             break;
                             } else if (circles.get(i).bTag == 2) {
                             double r1 = player.radius;
                             double r2 = circles.get(i).radius;

                             double newRadius = Math.pow(Math.abs(Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);
                             player.circle.setRadius(newRadius);
                             circles.get(i).circle.remove();
                             circles.remove(i);
                             break;
                             } else {

                             }

                             }

                             }

                             }
                             Random ran = new Random();
                             int ch = 0;

                             for(int i =0 ; i <circles.size();i++){

                             for (int j=0; j<bCircles.size();j++) {

                             double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, bCircles.get(j).circle.getCenter().latitude, bCircles.get(j).circle.getCenter().longitude);

                             if (circles.get(i).radius + bCircles.get(j).radius > Math.abs(distanceBwBubbles)) {

                             int hole = ((ran.nextInt(warmHoleCount)+0)%warmHoleCount);


                             while (hole==j)
                             hole = ((ran.nextInt(warmHoleCount)+0)%warmHoleCount);


                             Log.d("collision with warm hole", " " + j + " " + hole);

                             circles.get(i).circle.setCenter(bCircles.get(hole).circle.getCenter());
                             while (distFrom(circles.get(i).circle.getCenter().latitude,circles.get(i).circle.getCenter().longitude,bCircles.get(hole).circle.getCenter().latitude,bCircles.get(hole).circle.getCenter().longitude)<circles.get(i).circle.getRadius()+bCircles.get(hole).circle.getRadius())
                             {
                             circles.get(i).circle.setCenter(new LatLng(circles.get(i).circle.getCenter().latitude+Math.sin(Math.toRadians(circles.get(i).direction))*circles.get(i).speed,circles.get(i).circle.getCenter().longitude+Math.cos(Math.toRadians(circles.get(i).direction))*circles.get(i).speed));
                             Log.d("life is what you want it to be!!", " everything is awesome ");
                             }


                             ch = 1;
                             break;
                             }
                             }
                             if (ch==1)
                             break;
                             }






                             }
                             else{
                             String strName = null;
                             }

                             */


                        }
                    });
                }
            }, 1000, 200);
            locationManager.requestLocationUpdates(bestProvider, 33, 0, this); // once every second
        }
        else{

            gps.showSettingsAlert();
        }
        //Log.d("current location ", location.getLatitude()+ " " + location.getLongitude());

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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     */
    int checking=1;
    @Override
    public void onMapReady(GoogleMap map) {
        //   map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

//        updateMyLocation();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19.5f));




        // check if GPS enabled
        if(gps.canGetLocation()) {

            if (BoundaryType.equals("Large Boundary")) {

                Log.d("boundary is", "Large" + BoundaryType);

                width = 2 * 0.0003;
                height = 2 * 0.0003;
                if(InitialLoc != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));
                numberOfBubbles = 21;
                warmHoleCount = 3;
            } else if (BoundaryType.equals("Medium Boundary")) {
                Log.d("boundary is", "medium" + BoundaryType);

                width = 2 * 0.0002;
                height = 2 * 0.0002;
                numberOfBubbles = 15;
                warmHoleCount = 3;
                if (InitialLoc != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

            } else {

                Log.d("boundary is", "small" + BoundaryType);
                width = 2 * 0.0001;
                height = 2 * 0.0001;
                numberOfBubbles = 9;
                warmHoleCount = 3;
                if (InitialLoc != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

            }
            if(InitialLoc != null){

                map.addPolygon(new PolygonOptions()
                        .addAll(createRectangle(new LatLng(InitialLoc.latitude, InitialLoc.longitude), width / 2, height / 2))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(2));

                //Log.d(" gps ",gpsLocation.latitude + " fssfd "+ gpsLocation.longitude);
                UpdateBubbles(InitialLoc.latitude, InitialLoc.longitude, BoundaryType);
            }
            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }


    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {

        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mFillColor = Color.RED;

        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.onStyleChange();
        }
    }

    private void updateMapType(){

        mMap.setMapType(2);
        // 2 IS CONSTANT VALUE FOR SATELLITE MAP

    }

    private void UpdateBubbles(double lat,double lon,String BoundaryType){

        mMap.setOnMapLongClickListener(this);
        mFillColor = Color.BLUE;
        mStrokeColor = Color.BLACK;
        mWidth = 2;


        if(BoundaryType.equals("Large Boundary")){

            Log.d("boundary is","Large" + BoundaryType);

            width = 2*0.00030;
            height= 2*0.00030;
        }
        else if(BoundaryType.equals("Medium Boundary")){
            Log.d("boundary is","medium" + BoundaryType);

            width = 2*0.00020;
            height= 2*0.00020;

        }
        else{

            Log.d("boundary is","small" + BoundaryType);
            width = 2*0.00010;
            height= 2*0.00010;
        }
        Random r = new Random();

        Boolean[][] isPlacedBubble = new Boolean[10][10];

        for(int i=0;i<10;i++){

            for (int j=0;j<10;j++){

                isPlacedBubble[i][j]=false;

            }

        }
        // Creating Matter Bubbles

        for(int i=0;i<2*numberOfBubbles/3;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag=1;
            double radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y]) {
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;

            }
            mFillColor = Color.YELLOW;
            // mCircles.add(circles.get(i));
            if(BoundaryType.equals("Small Boundary") || BoundaryType.equals("Medium Boundary"))
            {circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), 0.7*radius,dir,tag,0.5*bubbleSpeed));
                isPlacedBubble[x][y]=true;}
            else{
                circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius,dir,tag,0.5*bubbleSpeed));
                isPlacedBubble[x][y]=true;
            }

        }



        // Creating Anti-Matter Bubbles

        for(int i=0;i<numberOfBubbles/3;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag = 2;
            double radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y]) {
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;

            }
            mFillColor = Color.GREEN;
            if(BoundaryType.equals("Small Boundary") || BoundaryType.equals("Medium Boundary")) {
                circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), 0.7 * radius, dir, tag, 0.5*bubbleSpeed));

            }
            else{

                circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius, dir, tag, 0.5*bubbleSpeed));

                // mCircles.add(circles.get(i));
            }
            isPlacedBubble[x][y]=true;

        }

        // Creating Warm-Holes
        int[] xCoordinate = new int[warmHoleCount];
        int[] yCoordinate = new int[warmHoleCount];
        int count = 0;
        for(int i=0;i<warmHoleCount;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag = 3;
            double radius = 2;
            int check = 1;
            for (int j=0;j<count;j++)
            {
                if (Math.sqrt(Math.pow(x-xCoordinate[j],2) + Math.pow(y-yCoordinate[j],2)) < 4.5)
                {
                    Log.d("hi","CHECKKKINNGGGG");
                    check = 0;
                    break;
                }
            }


            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y] || check==0) {
                check = 1;
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;
                for (int j=0;j<count;j++)
                {
                    if (Math.sqrt(Math.pow(x-xCoordinate[j],2) + Math.pow(y-yCoordinate[j],2)) < 4.5)
                    {


                        Log.d("hi","CHECKKKINNGGGG");
                        check = 0;
                        break;
                    }
                }


            }
            mFillColor = Color.BLACK;
            if(BoundaryType.equals("Large Boundary")) {
                bCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius, dir, tag, 0));
                // mCircles.add(circles.get(i));
                xCoordinate[count] = x;
                yCoordinate[count++] = y;
                isPlacedBubble[x][y] = true;
            }
            else{
                bCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12),0.6 * radius, dir, tag, 0));
                // mCircles.add(circles.get(i));
                xCoordinate[count] = x;
                yCoordinate[count++] = y;
                isPlacedBubble[x][y] = true;

            }

        }

        // Creating Repulsor Bubbles

        for(int i=0;i<1;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag = 4;
            double radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y]) {
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;

            }
            mFillColor = Color.RED;
            if(BoundaryType.equals("Small Boundary") || BoundaryType.equals("Medium Boundary")) {
                rCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), 0.7 * radius, dir, tag, 0.5*bubbleSpeed));

            }
            else{

                rCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius, dir, tag, 0.5*bubbleSpeed));

                // mCircles.add(circles.get(i));
            }

            isPlacedBubble[x][y]=true;

        }


    }

    public void onLocationChanged(Location location) {
        //  TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        gpsLocation = new LatLng(latitude,longitude);

        //    Log.d("location",latitude+ " ");
        totalscore+=0;
        score.setText("  SCORE :" + String.valueOf(totalscore));


        mMap.setOnMapLongClickListener(this);

        mFillColor = Color.MAGENTA;
        mStrokeColor = Color.BLACK;
        mWidth = 2;
        // PolygonOptions options = new PolygonOptions().addAll(createRectangle(playerLocation, 500, 8));
        Log.d(" location on changed is called ", gpsLocation.latitude + " lat " );
        //   PlayerBubble play = new PlayerBubble(gpsLocation, DEFAULT_RADIUS,mMap);
        if(checker==1) {
            DraggableCircle circle = new DraggableCircle(gpsLocation, 1.5 * DEFAULT_RADIUS, 0, 0, 0);
            checker+=1;
            mCircles.add(circle);
        }
        else {
            Log.d(" size of mcircles ", mCircles.size() + " size " + checker);
            if(mCircles.size()>0)
                mCircles.get(mCircles.size()-1).circle.setCenter(new LatLng(gpsLocation.latitude,gpsLocation.longitude));

        }
//            Log.d(" Else of size of mcircles ",mCircles.size()+ " size "+ checker );
        //               //  UpdateBubbles(latitude,longitude);
        //   play.circle.setCenter(new LatLng(latitude,longitude));


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }

        if (!mMyLocationCheckbox.isChecked()) {
            mMap.setMyLocationEnabled(false);
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            mMyLocationCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }


    @Override
    public void onMapLongClick(LatLng point) {

    }

    public void OnPlayerBoundaryCollission(LatLng InitialLoc,double height,double width){

        LatLng loca =  InitialLoc;
        double latit = loca.latitude;
        double longit = loca.longitude;

        DraggableCircle cir = mCircles.get(circles.size() - 1);

        if(distFrom(latit,longit,InitialLoc.latitude+width/2,longit) < cir.circle.getRadius()){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    TopView.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("CAUTION");

            // Setting Dialog Message
            alertDialog.setMessage("Move Back within boundary");


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();
            //    Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
        }
        else if(distFrom(latit,longit,latit,InitialLoc.longitude+width/2) < cir.circle.getRadius()){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    TopView.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("CAUTION1");

            // Setting Dialog Message
            alertDialog.setMessage("Move Back within boundary");


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();



            //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
        }
        else if(distFrom(latit,longit,InitialLoc.latitude-width/2,longit) < cir.circle.getRadius()){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    TopView.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("CAUTION1");

            // Setting Dialog Message
            alertDialog.setMessage("Move Back within boundary");


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();

            //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
        }
        else if(distFrom(latit,longit,latit,InitialLoc.longitude-width/2) < cir.circle.getRadius()){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    TopView.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("CAUTION1");

            // Setting Dialog Message
            alertDialog.setMessage("Move Back within boundary");


            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();




            //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
        }




    }

    public void OnCollisionBoundary(List<DraggableCircle> circles,LatLng InitialLoc,double height,double width){

        /*******Collission with Boundary ************/
        for(int i=0;i<circles.size();i++){
            // circles.g
            DraggableCircle cir = circles.get(i);
//                            Log.d("tagg",cir.toString());

            //            System.out.println(cir.circle.getCenter().latitude);
            LatLng loca =  cir.circle.getCenter();
            double latit = loca.latitude;
            double longit = loca.longitude;
            // Log.d("angle"," "+cir.direction);
            LatLng finalLoc = new LatLng(latit+Math.sin(Math.toRadians(cir.direction))*cir.speed,longit+Math.cos(Math.toRadians(cir.direction))*cir.speed);
            cir.circle.setCenter(finalLoc);
            //  Log.d("inital location", InitialLoc.latitude + " " + InitialLoc.longitude);
            // Log.d(" direction "," " +cir.direction);
            // Log.d("collision", latit + " " + longit + " "+ cir.direction + " " + (InitialLoc.latitude +width/2));
            double decFactor = 0.000005;
            if(distFrom(latit,longit,InitialLoc.latitude+width/2,longit) < cir.circle.getRadius()){

                cir.direction= 360-cir.direction;
                cir.circle.setCenter(new LatLng(latit-decFactor,longit));

                //    Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(distFrom(latit,longit,latit,InitialLoc.longitude+width/2) < cir.circle.getRadius()){

                if(cir.direction<180)
                    cir.direction= 180-cir.direction;
                else
                    cir.direction = 540-cir.direction;
                cir.circle.setCenter(new LatLng(latit,longit-decFactor));
                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(distFrom(latit,longit,InitialLoc.latitude-width/2,longit) < cir.circle.getRadius()){

                cir.direction= 360-cir.direction;

                cir.circle.setCenter(new LatLng(latit+decFactor,longit));

                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(distFrom(latit,longit,latit,InitialLoc.longitude-width/2) < cir.circle.getRadius()){

                if(cir.direction <180)
                    cir.direction= 180-cir.direction;
                else
                    cir.direction = 540-cir.direction;
                cir.circle.setCenter(new LatLng(latit,longit + decFactor));

                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }


        }

        /*******  -------  ************/



    }

    public void RepellerPhysics()
    {
        if (mCircles.size()>0) {
            if (speedx == -1.0) {
                speedx = rCircles.get(0).speed;
                speedy = speedx;
            }
            LatLng repLocation = new LatLng(rCircles.get(0).circle.getCenter().latitude + Math.sin(Math.toRadians(rCircles.get(0).direction)) * speedx, rCircles.get(0).circle.getCenter().longitude + Math.cos(Math.toRadians(rCircles.get(0).direction)) * speedy);

            rCircles.get(0).circle.setCenter(repLocation);


            for (int i = 0; i < rCircles.size(); i++) {

                double dist1 = rCircles.get(i).circle.getCenter().longitude - mCircles.get(0).circle.getCenter().longitude;
                double dist2 = rCircles.get(i).circle.getCenter().latitude - mCircles.get(0).circle.getCenter().latitude;

                double angle = Math.toDegrees(Math.atan(dist2 / dist1));

                double dist = distFrom(rCircles.get(i).circle.getCenter().latitude, rCircles.get(i).circle.getCenter().longitude, mCircles.get(0).circle.getCenter().latitude, mCircles.get(0).circle.getCenter().longitude);
                if (dist<10.0) {
                    if (speedx == -1.0) {
                        speedx = rCircles.get(i).speed;
                        speedy = speedx;
                    } else {
                        speedx += ((1 / (dist * dist)) * Math.sin(Math.toRadians(angle))) / 25000;
                        speedy += ((1 / (dist * dist)) * Math.cos(Math.toRadians(angle))) / 25000;
                    }
                    repLocation = new LatLng(rCircles.get(0).circle.getCenter().latitude + (Math.sin(Math.toRadians(rCircles.get(0).direction))) * (speedx), rCircles.get(0).circle.getCenter().longitude + (Math.cos(Math.toRadians(rCircles.get(0).direction))) * (speedy));
                    rCircles.get(0).circle.setCenter(repLocation);

                    rCircles.get(0).direction = (int) Math.toDegrees(Math.atan(speedy / speedx));
                }
            }


            for (int i = 0; i < rCircles.size(); i++) {
                // circles.g
                DraggableCircle cir = rCircles.get(i);
//                            Log.d("tagg",cir.toString());

                //            System.out.println(cir.circle.getCenter().latitude);
                LatLng loca = cir.circle.getCenter();
                double latit = loca.latitude;
                double longit = loca.longitude;
                // Log.d("angle"," "+cir.direction);
                //LatLng finalLoc = new LatLng(latit+Math.sin(Math.toRadians(cir.direction))*cir.speed,longit+Math.cos(Math.toRadians(cir.direction))*cir.speed);
                //cir.circle.setCenter(finalLoc);
                //  Log.d("inital location", InitialLoc.latitude + " " + InitialLoc.longitude);
                // Log.d(" direction "," " +cir.direction);
                // Log.d("collision", latit + " " + longit + " "+ cir.direction + " " + (InitialLoc.latitude +width/2));
                double decFactor = 0.000005;
                if (distFrom(latit, longit, InitialLoc.latitude + width / 2, longit) < cir.circle.getRadius()) {

                    cir.direction = 360 - cir.direction;
                    cir.circle.setCenter(new LatLng(latit - decFactor, longit));

                    //    Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                } else if (distFrom(latit, longit, latit, InitialLoc.longitude + width / 2) < cir.circle.getRadius()) {

                    if (cir.direction < 180)
                        cir.direction = 180 - cir.direction;
                    else
                        cir.direction = 540 - cir.direction;
                    cir.circle.setCenter(new LatLng(latit, longit - decFactor));
                    //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                } else if (distFrom(latit, longit, InitialLoc.latitude - width / 2, longit) < cir.circle.getRadius()) {

                    cir.direction = 360 - cir.direction;

                    cir.circle.setCenter(new LatLng(latit + decFactor, longit));

                    //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                } else if (distFrom(latit, longit, latit, InitialLoc.longitude - width / 2) < cir.circle.getRadius()) {

                    if (cir.direction < 180)
                        cir.direction = 180 - cir.direction;
                    else
                        cir.direction = 540 - cir.direction;
                    cir.circle.setCenter(new LatLng(latit, longit + decFactor));

                    //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                }


            }
        }
    }


    public void CollisionNonPlayerBubbles(List<DraggableCircle> circles,GoogleMap mMap){

        int check=0;
        /****** ----- Collision of non player bubbles ****/
        for(int i=0;i<circles.size();i++){

            for(int j=0;j<circles.size();j++){

                if(i!=j){
                    double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, circles.get(j).circle.getCenter().latitude, circles.get(j).circle.getCenter().longitude);

                    if(circles.get(i).radius+circles.get(j).radius>=Math.abs(distanceBwBubbles)){


                        double r1 = circles.get(i).radius;
                        double r2 = circles.get(j).radius;

                        double lat = r1>=r2?circles.get(i).circle.getCenter().latitude:circles.get(j).circle.getCenter().latitude;
                        double lon = r1>=r2?circles.get(i).circle.getCenter().longitude:circles.get(j).circle.getCenter().longitude;
                        int dir = r1>=r2?circles.get(i).direction:circles.get(j).direction;
                        int tag = r1>=r2?circles.get(i).bTag:circles.get(j).bTag;
                        double sp = r1>=r2?circles.get(i).speed:circles.get(j).speed;

                        double newRadius;
                        if (circles.get(i).bTag==circles.get(j).bTag)
                            newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                        else
                            newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                        //    Log.d("New Radius is"," "+newRadius);

                        //    Log.d("size of circles before removal", " "+ circles.size());
                        //circles.get(i).
                        if(i<j)
                            j-=1;
                        circles.get(i).circle.remove();

                        circles.remove(i);

                        circles.get(j).circle.remove();


                        circles.remove(j);
                        //  Log.d("size of circles", " "+ circles.size());
                        if (tag==1)
                            mFillColor = Color.YELLOW;
                        else
                            mFillColor = Color.GREEN;
                        circles.add(new DraggableCircle(new LatLng(lat,lon), newRadius,dir,tag,sp));
                        check=1;
                        break;
                    }


                }


            }

            if(check==1)
                break;



        }



    }

    public void CollisionPlayerNonplayer(List<DraggableCircle> circles ,List<DraggableCircle> mCircles, List<DraggableCircle> bCircles  ) {

        if (mCircles.size() > 0) {
            DraggableCircle player = mCircles.get(mCircles.size() - 1);

            for (int i = 0; i < circles.size(); i++) {

                double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, mCircles.get(mCircles.size() - 1).circle.getCenter().latitude, mCircles.get(mCircles.size() - 1).circle.getCenter().longitude);

                if (circles.get(i).radius + mCircles.get(mCircles.size() - 1).radius >= Math.abs(distanceBwBubbles)) {
                    Log.d("collision with player", " " + circles.get(i).bTag);
                    if (player.radius < circles.get(i).radius) {
                        int size = mCircles.size();
                        mCircles.get(size - 1).circle.remove();
                        mCircles.remove(size - 1);

                        /** Game Over Here **/

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                TopView.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("OOOPSSS!!!");
                        Log.d("Game is "," Over !!!");
                        // Setting Dialog Message

                        alertDialog.setMessage("You scored " + String.valueOf(totalscore) + " .Game Over!!");

                        totalscore=0;

                        // Setting OK Button
                        alertDialog.setButton("START AGAIN", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(new Intent(TopView.this, MainActivity.class));
                                Toast.makeText(getApplicationContext(), "You clicked on RESTART", Toast.LENGTH_SHORT).show();
                                gameover=true;
                                finish();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();



                        break;
                    } else {
                        if (circles.get(i).bTag == 1) {
                            double r1 = player.radius;
                            double r2 = circles.get(i).radius;

                            double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);

                            totalscore+=20;
                            score.setText("  SCORE :" + String.valueOf(totalscore));

                            player.circle.setRadius(newRadius);
                            circles.get(i).circle.remove();
                            circles.remove(i);
                            break;
                        } else if (circles.get(i).bTag == 2) {
                            double r1 = player.radius;
                            double r2 = circles.get(i).radius;

                            double newRadius = Math.pow(Math.abs(Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);
                            player.circle.setRadius(newRadius);

                            totalscore-=10;
                            score.setText("  SCORE :" + String.valueOf(totalscore));

                            circles.get(i).circle.remove();
                            circles.remove(i);
                            break;
                        } else {

                        }

                    }

                }

            }
            Random ran = new Random();
            int ch = 0;

            for (int i = 0; i < circles.size(); i++) {

                for (int j = 0; j < bCircles.size(); j++) {

                    double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, bCircles.get(j).circle.getCenter().latitude, bCircles.get(j).circle.getCenter().longitude);

                    if (circles.get(i).radius + bCircles.get(j).radius > Math.abs(distanceBwBubbles)) {

                        int hole = ((ran.nextInt(warmHoleCount) + 0) % warmHoleCount);


                        while (hole == j)
                            hole = ((ran.nextInt(warmHoleCount) + 0) % warmHoleCount);


                        Log.d("collision with warm hole", " " + j + " " + hole);

                        circles.get(i).circle.setCenter(bCircles.get(hole).circle.getCenter());
                        while (distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, bCircles.get(hole).circle.getCenter().latitude, bCircles.get(hole).circle.getCenter().longitude) < circles.get(i).circle.getRadius() + bCircles.get(hole).circle.getRadius()) {
                            circles.get(i).circle.setCenter(new LatLng(circles.get(i).circle.getCenter().latitude + Math.sin(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed, circles.get(i).circle.getCenter().longitude + Math.cos(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed));
                            Log.d("life is what you want it to be!!", " everything is awesome ");
                        }


                        ch = 1;
                        break;
                    }
                }
                if (ch == 1)
                    break;
            }


        } else {
            String strName = null;
        }


    }

}