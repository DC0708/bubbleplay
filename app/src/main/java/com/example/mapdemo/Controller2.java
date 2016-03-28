package com.example.mapdemo;


import com.example.mapdemo.GeolocationService;
import com.example.mapdemo.Model;
import com.example.mapdemo.PermissionUtils;
import com.example.mapdemo.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DC0708 on 31-Jan-16.
 */
public class Controller2 extends AppCompatActivity implements LocationListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, OnRequestPermissionsResultCallback {


    @Override
    protected void onStart(){

        super.onStart();
        Log.d("controller on","start");
    }

    @Override
    protected void onRestart(){

        Log.d("controller on", "restart");
        super.onRestart();
    }

    private LatLng InitialLoc;

    private LatLng SourceLoc;

    GoogleMap googleMap;
    public LatLng gpsLocation;

    public int gpschecker =1;

    private CheckBox mMyLocationCheckbox;

    public final double mediumfactor = 1.5;
    public final double largefactor = 2.0;

    double speedx = -1.0;

    double speedy = -1.0;

    double repellerSafeDistance = 10.0;

    private static final double DEFAULT_RADIUS = 0.5;
    public int totalscore = 0;

    GeolocationService gps;

    protected LocationListener locationListener;
    protected LocationManager locationManager;

    public Model gamemodel;
    TextView score;

    public Location location;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public String BoundaryType;

    public String bestProvider = LocationManager.GPS_PROVIDER;

    public Circle Player;

    double playerradii = DEFAULT_RADIUS;

    public List<Circle> otherPlayers = new ArrayList<Circle>(10);

    public List<SnackBubble> snacks = new ArrayList<SnackBubble>(20);

    public List<JunkBubble> junks = new ArrayList<JunkBubble>(20);

    public List<WormHole> holes = new ArrayList<WormHole>(20);

    public List<Circle> snac = new ArrayList<Circle>(10);

    public List<Circle> jun = new ArrayList<Circle>(10);

    public List<Circle> repe = new ArrayList<Circle>(10);

    public RepellerBubble repeller;

    public String Gamemode;

    public String Challengeid;

    public String Playermode;

    public boolean gameover = false;

    public Boolean isGPSEnabled = false;

    public Boolean isNetworkEnabled = false;

    SharedPreferences shrd;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        shrd = getApplicationContext().getSharedPreferences("UserSession",0);


        gps = new GeolocationService(Controller2.this);

        score =(TextView) findViewById(R.id.description);

        score.setText("  SCORE :" + String.valueOf(totalscore));

        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        if(gps.canGetLocation){
            Log.d("Isin","if");
        }
        else{
            Log.d("Isin","else");
            gps.showSettingsAlert(Controller2.this);
        }

        final Bundle extras = getIntent().getExtras();

        if(extras != null) {
            Challengeid = extras.getString("challengeid");

            BoundaryType = "Large";//extras.getString("Boundary");
            Gamemode = "biggest";//extras.getString("gamemode");
            Playermode = extras.getString("playermode");

            if(extras.containsKey("sourcelat")){
                Log.d("lat and long", " " + extras.getDouble("sourcelat") + extras.getDouble("sourcelong"));

                SourceLoc = new LatLng(extras.getDouble("sourcelat"),extras.getDouble("sourcelong"));

            }

        }

        Log.d("Game mode is", Gamemode);

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMap = mapFragment.getMap();

        googleMap.setMyLocationEnabled(false);

        updateMapType();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Getting GPS status
        if(locationManager!=null){

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(!isGPSEnabled){
//                Log.d("in GPS settings","show settings");
                gps.showSettingsAlert(Controller2.this);
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

            locationManager.requestLocationUpdates(bestProvider, 20, 0, this);
            onLocationChanged(location);
            FetchGps();

            gamemodel = new Model(BoundaryType);
            if(isGPSEnabled){

                createboundary(BoundaryType);
            }
            else{
                gps.showSettingsAlert(Controller2.this);
            }
            Log.d("challenger  is : ", Challengeid);
            createsnackbubbles();
            createjunkbubbles();
            createwormholes();
            createOtherPlayers();
            if(Gamemode.equals("repulsor"))
                createrepeller();


            final Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run(){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){



                            OnCollisionBoundary();
                            CollisionNonPlayerBubbles();
                            CollisionPlayerNonplayer();
                            CollisionWormHole();
                            updatePlayerLocations();
                            if(Gamemode.equals("repulsor"))
                                RepellerPhysics();

                            // repeller winning condition
                            if (Gamemode.equals("repulsor")) {
                                if (Player.getRadius() + repeller.radius >= Math.abs(distFrom(Player.getCenter().latitude, Player.getCenter().longitude, repeller.center.x, repeller.center.y))) {

                                    Intent i = new Intent(Controller2.this, EndGame.class);
                                    i.putExtra("totalscore", String.valueOf(totalscore));
                                    i.putExtra("gameresult","won");
                                    startActivity(i);
//                                    finish();
                                    timer.cancel();
                                    timer.purge();
                                    return;

                                }

                            }

                            else {

                                if(gameover==true){

                                    updatePlayerLost();
                                    Intent i = new  Intent(Controller2.this,EndGame.class);
                                    i.putExtra("gameresult","lost");
                                    startActivity(i);
                                    timer.cancel();
                                    timer.purge();
                                    //send to intent and player lost!!!..
                                }
                                else{

                                    if(otherPlayers.size()<=0){

                                        Intent i = new  Intent(Controller2.this,EndGame.class);
                                        i.putExtra("gameresult","won");
                                        startActivity(i);
                                        timer.cancel();
                                        timer.purge();

                                        //send to intent with this player has won the game!!!..

                                    }

                                }

/*
                                // biggest bubble condition
                                double maxRadius = -1;

                                for (int i = 0; i < snac.size(); i++) {

                                    if (snac.get(i).getRadius() > maxRadius)
                                        maxRadius = snac.get(i).getRadius();
                                }

                                if (Player.getRadius() > maxRadius) {

                                    Intent i = new Intent(Controller2.this, EndGame.class);
                                    i.putExtra("totalscore", String.valueOf(totalscore));
                                    i.putExtra("gameresult","won");
                                    startActivity(i);
                                    //                                  finish();
                                    timer.cancel();
                                    timer.purge();
                                    return;
                                }
*/
                            }


                            // losing condition **
/*                            if(gameover){


                                Intent i = new Intent(Controller2.this, EndGame.class);
                                i.putExtra("totalscore",String.valueOf(totalscore));
                                i.putExtra("gameresult","lost");

                                startActivity(i);
                                //                            finish();
                                timer.cancel();
                                timer.purge();
                                return;

                            }
*/
                        }
                    });
                }
            }, 1000, 200);




        }
        else{

            gps.showSettingsAlert(Controller2.this);
        }

        Log.d("Here","asdsa");
        Log.d("Here", "asdsa");

    }



    @Override
    public void onMapReady(GoogleMap map) {

        score =(TextView) findViewById(R.id.description);

        score.setText("SCORE :" + String.valueOf(totalscore));

    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        gpsLocation = new LatLng(latitude,longitude);

        totalscore = totalscore + 3;
        score.setText(" SCORE : " + totalscore);


        googleMap.setOnMapLongClickListener(this);

        Log.d(" location on changed is called ", gpsLocation.latitude + " lat " );
        //   PlayerBubble play = new PlayerBubble(gpsLocation, DEFAULT_RADIUS,mMap);
        if(gpschecker==1) {

            if(BoundaryType.equals("Small")){
                playerradii = DEFAULT_RADIUS;
            }
            else if(BoundaryType.equals("Medium")){
                playerradii = mediumfactor * DEFAULT_RADIUS;
            }
            else{
                playerradii = largefactor * DEFAULT_RADIUS;
            }
            CircleOptions temp3 = new CircleOptions()
                    .center(gpsLocation)
                    .radius(playerradii)
                    .strokeWidth(2)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.MAGENTA);
            Player = googleMap.addCircle(temp3);
            gpschecker+=1;
        }
        else {
            //if(mCircles.size()>0)
            //mCircles.get(mCircles.size()-1).circle.setCenter(new LatLng(gpsLocation.latitude,gpsLocation.longitude));
            Player.setCenter(gpsLocation);
        }

        new Thread(new Runnable() {
            public void run(){


                String data1 = "";
                // Create data variable for sent values to server
                try {

                    data1 += "&" + URLEncoder.encode("username", "UTF-8")
                            + "=" + URLEncoder.encode(shrd.getString("username","username"), "UTF-8");
                    data1 += "&" + URLEncoder.encode("latitude", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(gpsLocation.latitude), "UTF-8");
                    data1 += "&" + URLEncoder.encode("longitude", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(gpsLocation.longitude), "UTF-8");
                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                BufferedReader reader1=null;

                // Send data
                try
                {
                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "updateiniloc.php");

                    // Send POST data request
                    Log.d("its pushh:", "notification !!");
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data1);
                    wr.flush();

                    // Get the server response

                    reader1 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while((line = reader1.readLine()) != null)
                    {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder+=line;

                    }


                    final String text = builder;
                    System.out.println("Textii: "+text);
                    System.out.println("len: "+text.length());
                    if (!text.equals("failure"))
                    {

                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "location in Controller2 " + text, Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                    else
                    {
                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "Error22: " + text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    try
                    {

                        reader1.close();
                    }

                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }



        }).start();


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

    public void OnCollisionBoundary() {

        /******* Collission with Boundary of snack bubble ********/
        for (int i = 0; i < snacks.size(); i++){

            SnackBubble snack = snacks.get(i);
            Circle tempsnack = snac.get(i);
            Centre loca = snack.center;
            double latit = loca.x;
            double longit = loca.y;

            LatLng finalLoc = new LatLng(latit + Math.sin(Math.toRadians(snack.dir)) * snack.speed, longit + Math.cos(Math.toRadians(snack.dir)) * snack.speed);
            //          Log.d("moved","no"+ snack.speed + " dir "+ snack.dir);
            snack.center.x = finalLoc.latitude;
            snack.center.y = finalLoc.longitude;
            tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude));
            //        Log.d("moved","yes");
            double decFactor = 0.000005;
            if (distFrom(latit, longit, InitialLoc.latitude + gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;
                snack.center.x = latit-decFactor;
                snack.center.y = longit;
                tempsnack.setCenter(new LatLng(finalLoc.latitude-decFactor,finalLoc.longitude));
            } else if (distFrom(latit, longit, latit, InitialLoc.longitude + gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;

                snack.dir = 360 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit-decFactor;
                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude-decFactor));

            } else if (distFrom(latit, longit, InitialLoc.latitude - gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;

                snack.center.x = latit+decFactor;
                snack.center.y = longit;

                tempsnack.setCenter(new LatLng(finalLoc.latitude+decFactor,finalLoc.longitude));

            } else if (distFrom(latit, longit, latit, InitialLoc.longitude - gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit+decFactor;

                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude+decFactor));

            }
        }

        /******** Collission with Boundary of junk bubble ************/
        for (int i = 0; i < junks.size(); i++){

            JunkBubble snack = junks.get(i);
            Circle tempsnack = jun.get(i);
            Centre loca = snack.center;
            double latit = loca.x;
            double longit = loca.y;

            LatLng finalLoc = new LatLng(latit + Math.sin(Math.toRadians(snack.dir)) * snack.speed, longit + Math.cos(Math.toRadians(snack.dir)) * snack.speed);
            // Log.d("moved","no"+ snack.speed + " dir "+ snack.dir);
            snack.center.x = finalLoc.latitude;
            snack.center.y = finalLoc.longitude;
            tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude));
            //
            // Log.d("moved","yes");
            double decFactor = 0.000005;
            if (distFrom(latit, longit, InitialLoc.latitude + gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;
                snack.center.x = latit-decFactor;
                snack.center.y = longit;
                tempsnack.setCenter(new LatLng(finalLoc.latitude-decFactor,finalLoc.longitude));
            } else if (distFrom(latit, longit, latit, InitialLoc.longitude + gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;

                snack.dir = 360 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit-decFactor;
                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude-decFactor));

            } else if (distFrom(latit, longit, InitialLoc.latitude - gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;

                snack.center.x = latit+decFactor;
                snack.center.y = longit;

                tempsnack.setCenter(new LatLng(finalLoc.latitude+decFactor,finalLoc.longitude));

            } else if (distFrom(latit, longit, latit, InitialLoc.longitude - gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit+decFactor;

                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude+decFactor));

            }
        }

    }

    public void CollisionNonPlayerBubbles(){

        int check=0;
        /****** ----- Collision of non player bubbles ****/
        for(int i=0;i<jun.size();i++){
            Circle temp = jun.get(i);
            for(int j=0;j<jun.size();j++){
                Circle temp2 = jun.get(j);

                if(i!=j){
                    double distanceBwBubbles = distFrom(temp.getCenter().latitude, temp.getCenter().longitude, temp2.getCenter().latitude, temp2.getCenter().longitude);

                    if(temp.getRadius()+temp2.getRadius()>=Math.abs(distanceBwBubbles)){


                        double r1 = temp.getRadius();
                        double r2 = temp2.getRadius();

                        double lat = r1>=r2?temp.getCenter().latitude:temp.getCenter().latitude;
                        double lon = r1>=r2?temp.getCenter().longitude:temp2.getCenter().longitude;
                        int dir = r1>=r2?junks.get(i).dir:junks.get(j).dir;
                        int tag = r1>=r2?junks.get(i).boolTag:junks.get(j).boolTag;
                        double sp = r1>=r2?junks.get(i).speed:junks.get(j).speed;

                        double newRadius;
                        //if (snacks.get(i).boolTag==snacks.get(j).boolTag)
                        newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                        //else
                        //   newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                        //    Log.d("New Radius is"," "+newRadius);

                        //    Log.d("size of circles before removal", " "+ circles.size());
                        //circles.get(i).
                        if(i<j) {
                            j -= 1;

                            Log.d("lol","hua");
                        }

                        //snac.get(i).remove();

                        Log.d("size junks initial ",jun.size() + " " + junks.size());

//                        snac.remove(i);
                        jun.get(i).remove();
                        jun.remove(i);
                        junks.remove(i);

                        Log.d("junk size " , jun.size() + " " + junks.size());

                        jun.get(j).remove();
                        jun.remove(j);
                        junks.remove(j);

                        Log.d(" junk size 2 " , jun.size() + " " + junks.size());


                        JunkBubble tempsnack = new JunkBubble(newRadius,dir,sp,new LatLng(lat,lon));
                        // snacks.add(tempsnack);

                        CircleOptions temp3 = new CircleOptions()
                                .center(new LatLng(lat,lon))
                                .radius(newRadius)
                                .strokeWidth(tempsnack.mWidth)
                                .strokeColor(tempsnack.mStrokeColor)
                                .fillColor(tempsnack.mFillColor);

                        Circle temp4 = googleMap.addCircle(temp3);
                        jun.add(temp4);
                        junks.add(tempsnack);

                        break;
                    }


                }


            }

            if(check==1)
                break;

        }



        /** snack collission **/
        for(int i=0;i<snac.size();i++){
            Circle temp = snac.get(i);
            for(int j=0;j<snac.size();j++){
                Circle temp2 = snac.get(j);

                if(i!=j){
                    double distanceBwBubbles = distFrom(temp.getCenter().latitude, temp.getCenter().longitude, temp2.getCenter().latitude, temp2.getCenter().longitude);

                    if(temp.getRadius()+temp2.getRadius()>=Math.abs(distanceBwBubbles)){


                        double r1 = temp.getRadius();
                        double r2 = temp2.getRadius();

                        double lat = r1>=r2?temp.getCenter().latitude:temp2.getCenter().latitude;
                        double lon = r1>=r2?temp.getCenter().longitude:temp2.getCenter().longitude;
                        int dir = r1>=r2?snacks.get(i).dir:snacks.get(j).dir;
                        int tag = r1>=r2?snacks.get(i).boolTag:snacks.get(j).boolTag;
                        double sp = r1>=r2?snacks.get(i).speed:snacks.get(j).speed;

                        double newRadius;
                        //if (snacks.get(i).boolTag==snacks.get(j).boolTag)
                        newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                        //else
                        //   newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                        //    Log.d("New Radius is"," "+newRadius);

                        //    Log.d("size of circles before removal", " "+ circles.size());
                        //circles.get(i).
                        if(i<j) {
                            j -= 1;

                            Log.d("lol","hua");
                        }

                        Log.d("size snacks initial ",snac.size() + " " + snacks.size());

//                        snac.remove(i);
                        snac.get(i).remove();
                        snac.remove(i);
                        snacks.remove(i);

                        Log.d("snac size " , snac.size() + " " + snacks.size());

                        snac.get(j).remove();
                        snac.remove(j);
                        snacks.remove(j);

                        Log.d(" snac size 2 " , snac.size() + " " + snacks.size());


                        SnackBubble tempsnack = new SnackBubble(newRadius,dir,sp,new LatLng(lat,lon));
                        // snacks.add(tempsnack);

                        CircleOptions temp3 = new CircleOptions()
                                .center(new LatLng(lat,lon))
                                .radius(newRadius)
                                .strokeWidth(tempsnack.mWidth)
                                .strokeColor(tempsnack.mStrokeColor)
                                .fillColor(tempsnack.mFillColor);

                        Circle temp4 = googleMap.addCircle(temp3);
                        snac.add(temp4);
                        snacks.add(tempsnack);

                        break;
                    }


                }


            }

            if(check==1)
                break;

        }

        check=0;
        /****** Collision of non player bubbles ****/
        for(int i=0;i<snac.size();i++){
            Circle temp = snac.get(i);
            for(int j=0;j<jun.size();j++){
                Circle temp2 = jun.get(j);

                double distanceBwBubbles = distFrom(temp.getCenter().latitude, temp.getCenter().longitude, temp2.getCenter().latitude, temp2.getCenter().longitude);

                if(temp.getRadius()+temp2.getRadius()>=Math.abs(distanceBwBubbles)){


                    double r1 = temp.getRadius();
                    double r2 = temp2.getRadius();

                    double lat = r1>=r2?temp.getCenter().latitude:temp2.getCenter().latitude;
                    double lon = r1>=r2?temp.getCenter().longitude:temp2.getCenter().longitude;
                    int dir = r1>=r2?snacks.get(i).dir:junks.get(j).dir;
                    int tag = r1>=r2?snacks.get(i).boolTag:junks.get(j).boolTag;
                    double sp = r1>=r2?snacks.get(i).speed:junks.get(j).speed;

                    double newRadius;
                    newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                    //    Log.d("New Radius is"," "+newRadius);

                    //    Log.d("size of circles before removal", " "+ circles.size());
                    //circles.get(i).
                    //snac.get(i).remove();
                    Log.d("size snacks initial ",snac.size() + " " + snacks.size());
//
                    snac.get(i).remove();
                    snac.remove(i);
                    snacks.remove(i);
                    Log.d("snac size 11 " , snac.size() + " " + snacks.size());

                    jun.get(j).remove();
                    jun.remove(j);

                    junks.remove(j);
                    Log.d("snac size 22 " , jun.size() + " " + junks.size());

                    if(tag==1){
                        SnackBubble tempsnack = new SnackBubble(newRadius, dir,sp,new LatLng(lat,lon));
                        // snacks.add(tempsnack);

                        CircleOptions temp3 = new CircleOptions()
                                .center(new LatLng(lat,lon))
                                .radius(newRadius)
                                .strokeWidth(tempsnack.mWidth)
                                .strokeColor(tempsnack.mStrokeColor)
                                .fillColor(tempsnack.mFillColor);

                        Circle temp4 = googleMap.addCircle(temp3);
                        snac.add(temp4);
                        snacks.add(tempsnack);

                    }
                    else{
                        JunkBubble tempjunk = new JunkBubble(newRadius, dir,sp,new LatLng(lat,lon));
                        // snacks.add(tempsnack);

                        CircleOptions temp3 = new CircleOptions()
                                .center(new LatLng(lat,lon))
                                .radius(newRadius)
                                .strokeWidth(tempjunk.mWidth)
                                .strokeColor(tempjunk.mStrokeColor)
                                .fillColor(tempjunk.mFillColor);

                        Circle temp4 = googleMap.addCircle(temp3);
                        jun.add(temp4);
                        junks.add(tempjunk);




                    }
                    break;
                }

            }

            if(check==1)
                break;

        }

    }

    public void RepellerPhysics(){

        if (speedx == -1.0) {
            speedx = repeller.speed;
            speedy = speedx;
        }

        LatLng repLocation = new LatLng(repeller.center.x + Math.sin(Math.toRadians(repeller.dir)) * speedx, repeller.center.y + Math.cos(Math.toRadians(repeller.dir)) * speedy);

        repeller.center.x = repLocation.latitude;
        repeller.center.y = repLocation.longitude;

        repe.get(0).setCenter(repLocation);

        for (int i = 0; i < repe.size(); i++) {

            double dist1 = repe.get(i).getCenter().longitude - Player.getCenter().longitude ;
            double dist2 = repe.get(i).getCenter().latitude - Player.getCenter().latitude;

            double angle = Math.toDegrees(Math.atan(dist2 / dist1));

            double dist = distFrom(repe.get(i).getCenter().latitude, repe.get(i).getCenter().longitude, Player.getCenter().latitude, Player.getCenter().longitude);
            if (dist<repellerSafeDistance) {
                if (speedx == -1.0) {
                    speedx = repeller.speed;
                    speedy = speedx;
                } else {
                    if (speedx<0.8E-5 && speedy < 0.8E-5) {
                        speedx += ((1 / (dist * dist)) * Math.sin(Math.toRadians(angle))) / 25000;
                        speedy += ((1 / (dist * dist)) * Math.cos(Math.toRadians(angle))) / 25000;
                    }
                    Log.d("speedx:"," " + speedx);
                    Log.d("speedy:"," " + speedy);

                }
                repLocation = new LatLng(repeller.center.x + (Math.sin(Math.toRadians(repeller.dir))) * (speedx), repeller.center.y + (Math.cos(Math.toRadians(repeller.dir))) * (speedy));
                repe.get(0).setCenter(repLocation);
                repeller.center.x = repLocation.latitude;
                repeller.center.y = repLocation.longitude;

                repeller.dir = (int) Math.toDegrees(Math.atan(speedy / speedx));
            }

            Log.d("Repeller ki", " Physics h!");
        }

        for (int i = 0; i < repe.size(); i++){

            RepellerBubble snack = repeller;
            Circle tempsnack = repe.get(i);
            Centre loca = repeller.center;
            double latit = loca.x;
            double longit = loca.y;

            LatLng finalLoc = new LatLng(latit + Math.sin(Math.toRadians(snack.dir)) * snack.speed, longit + Math.cos(Math.toRadians(snack.dir)) * snack.speed);
            //          Log.d("moved","no"+ snack.speed + " dir "+ snack.dir);
            snack.center.x = finalLoc.latitude;
            snack.center.y = finalLoc.longitude;
            tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude));
            //        Log.d("moved","yes");
            double decFactor = 0.000005;
            if (distFrom(latit, longit, InitialLoc.latitude + gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;
                snack.center.x = latit-decFactor;
                snack.center.y = longit;
                tempsnack.setCenter(new LatLng(finalLoc.latitude-decFactor,finalLoc.longitude));
            } else if (distFrom(latit, longit, latit, InitialLoc.longitude + gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;

                snack.dir = 360 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit-decFactor;
                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude-decFactor));

            } else if (distFrom(latit, longit, InitialLoc.latitude - gamemodel.boundaryWidth / 2, longit) < snack.radius) {

                snack.dir = 360 - snack.dir;

                snack.center.x = latit+decFactor;
                snack.center.y = longit;

                tempsnack.setCenter(new LatLng(finalLoc.latitude+decFactor,finalLoc.longitude));

            } else if (distFrom(latit, longit, latit, InitialLoc.longitude - gamemodel.boundaryWidth / 2) < snack.radius) {

                if (snack.dir < 180)
                    snack.dir = 180 - snack.dir;
                else
                    snack.dir = 540 - snack.dir;
                snack.center.x = latit;
                snack.center.y = longit+decFactor;

                tempsnack.setCenter(new LatLng(finalLoc.latitude,finalLoc.longitude+decFactor));

            }
        }

    }


    public void updatePlayerLost(){

        new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(shrd.getString("username","username"), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "updatePlayerLost.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.



                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();








    }

    public void updatePlayerLocations()
    {
        new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(shrd.getString("username","username"), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "updateOtherPlayers.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.

                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int pq = 0; pq < jsonArray.length(); pq++) {
                                    try {
                                        double centerlat = jsonArray.getJSONObject(pq).getDouble("latitude");

                                        double centerlong = jsonArray.getJSONObject(pq).getDouble("longitude");

                                        Log.d("other players size"," "+ otherPlayers.size() +" " +  gameover);
//                                        if (otherPlayers.size()>pq-1)
                                        otherPlayers.get(pq).setCenter(new LatLng(centerlat,centerlong));

                                        System.out.println("All players location updated");
                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //till here

//                    String[] response = text.split("-");
//                    //System.out.println("Text: "+text);
//                    //System.out.println("len: "+text.length());
//                    if (response[0].equals("success")) {
//                        //    SharedPreferences pref;
//                        //    SharedPreferences.Editor editor;
//                        //    pref = getApplicationContext().getSharedPreferences("UserSession", 0);
//                        System.out.println(response[0].toString());
//                        System.out.println("Logging in!");
//                    } else {
//                    /*    JSONObject json_data = new JSONObject(text);
//                        int userId = Integer.parseInt(json_data.getString("snackid"));
//
//                        System.out.println("json" + json_data.toString() + " Ahaa " + userId);
//*/
//                        Controller2.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(Controller2.this, "Error: " + text, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();

    }

    public void createOtherPlayers(){

        Thread t = new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(shrd.getString("username","username"), "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "updateOtherPlayers.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.

                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int pq = 0; pq < jsonArray.length(); pq++) {
                                    try {
                                        double centerlat = jsonArray.getJSONObject(pq).getDouble("latitude");

                                        double centerlong = jsonArray.getJSONObject(pq).getDouble("longitude");

                                        CircleOptions temp3 = new CircleOptions()
                                                .center(gpsLocation)
                                                .radius(playerradii)
                                                .strokeWidth(2)
                                                .strokeColor(Color.BLACK)
                                                .fillColor(Color.GRAY);
                                        Circle otherPlayer;
                                        otherPlayer = googleMap.addCircle(temp3);
                                        otherPlayers.add(otherPlayer);

                                        Log.d("yooooooooooo", "i am heree@@@@@@@@@@@@");
                                       // System.out.println("snackid: " + jsonArray.getJSONObject(pq).getInt("snackid"));

                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //till here

                    String[] response = text.split("-");
                    //System.out.println("Text: "+text);
                    //System.out.println("len: "+text.length());
                    if (response[0].equals("success")) {
                        //    SharedPreferences pref;
                        //    SharedPreferences.Editor editor;
                        //    pref = getApplicationContext().getSharedPreferences("UserSession", 0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    } else {
                    /*    JSONObject json_data = new JSONObject(text);
                        int userId = Integer.parseInt(json_data.getString("snackid"));

                        System.out.println("json" + json_data.toString() + " Ahaa " + userId);
*/
                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        t.start();
        try {
            t.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();

        }
    }

    public void createsnackbubbles(){


        new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "fetchsnack.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.

                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int pq = 0; pq < jsonArray.length(); pq++) {
                                    try {
                                        int snackid = jsonArray.getJSONObject(pq).getInt("snackid");

                                        int dir = jsonArray.getJSONObject(pq).getInt("dir");

                                        double centerlat = jsonArray.getJSONObject(pq).getDouble("centerlat");

                                        double centerlong = jsonArray.getJSONObject(pq).getDouble("centerlong");

                                        double radius = jsonArray.getJSONObject(pq).getDouble("radius");

                                        double speed = jsonArray.getJSONObject(pq).getDouble("speed");

                                        Log.d("yooooooooooo", "i am heree@@@@@@@@@@@@");
                                        System.out.println("snackid: " + jsonArray.getJSONObject(pq).getInt("snackid"));
                                        SnackBubble tempsnack = new SnackBubble(radius, dir, speed, new LatLng(centerlat, centerlong));
                                        snacks.add(tempsnack);

                                        CircleOptions temp = new CircleOptions()
                                                .center(new LatLng(tempsnack.center.x, tempsnack.center.y))
                                                .radius(tempsnack.radius)
                                                .strokeWidth(tempsnack.mWidth)
                                                .strokeColor(tempsnack.mStrokeColor)
                                                .fillColor(tempsnack.mFillColor);


                                        Circle temp1 = googleMap.addCircle(temp);
                                        snac.add(temp1);
                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //till here

                    String[] response = text.split("-");
                    //System.out.println("Text: "+text);
                    //System.out.println("len: "+text.length());
                    if (response[0].equals("success")) {
                    //    SharedPreferences pref;
                    //    SharedPreferences.Editor editor;
                    //    pref = getApplicationContext().getSharedPreferences("UserSession", 0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    } else {
                    /*    JSONObject json_data = new JSONObject(text);
                        int userId = Integer.parseInt(json_data.getString("snackid"));

                        System.out.println("json" + json_data.toString() + " Ahaa " + userId);
*/
                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();


    }



    public void createjunkbubbles(){


        new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "fetchjunk.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.

                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int pq = 0; pq < jsonArray.length(); pq++) {
                                    try {
                                        int snackid = jsonArray.getJSONObject(pq).getInt("junkid");

                                        int dir = jsonArray.getJSONObject(pq).getInt("dir");

                                        double centerlat = jsonArray.getJSONObject(pq).getDouble("centerlat");

                                        double centerlong = jsonArray.getJSONObject(pq).getDouble("centerlong");

                                        double radius = jsonArray.getJSONObject(pq).getDouble("radius");

                                        double speed = jsonArray.getJSONObject(pq).getDouble("speed");

                                        Log.d("yooooooooooo", "i am heree@@@@@@@@@@@@");
                                        System.out.println("snackid: " + jsonArray.getJSONObject(pq).getInt("junkid"));
                                        JunkBubble tempsnack = new JunkBubble(radius, dir, speed, new LatLng(centerlat, centerlong));
                                        junks.add(tempsnack);

                                        CircleOptions temp = new CircleOptions()
                                                .center(new LatLng(tempsnack.center.x, tempsnack.center.y))
                                                .radius(tempsnack.radius)
                                                .strokeWidth(tempsnack.mWidth)
                                                .strokeColor(tempsnack.mStrokeColor)
                                                .fillColor(tempsnack.mFillColor);


                                        Circle temp1 = googleMap.addCircle(temp);
                                        jun.add(temp1);
                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //till here

                    String[] response = text.split("-");
                    //System.out.println("Text: "+text);
                    //System.out.println("len: "+text.length());
                    if (response[0].equals("success")) {
                        //    SharedPreferences pref;
                        //    SharedPreferences.Editor editor;
                        //    pref = getApplicationContext().getSharedPreferences("UserSession", 0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    } else {
                    /*    JSONObject json_data = new JSONObject(text);
                        int userId = Integer.parseInt(json_data.getString("snackid"));

                        System.out.println("json" + json_data.toString() + " Ahaa " + userId);
*/
                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();


    }

    public void createwormholes(){


        new Thread (new Runnable() {

            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(Challengeid, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;

                // Send data
                try {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "fetchhole.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder += line;

                    }


                    final String text = builder;

                    // Object is a single row of the json array.
                    // you can fetch all the details like snack iDs are fetched below.

                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int pq = 0; pq < jsonArray.length(); pq++) {
                                    try {

                                        double centerlat = jsonArray.getJSONObject(pq).getDouble("centerlat");

                                        double centerlong = jsonArray.getJSONObject(pq).getDouble("centerlong");

                                        double radius = jsonArray.getJSONObject(pq).getDouble("radius");

                                        Log.d("yooooooooooo", "i am heree@@@@@@@@@@@@");
//                                        System.out.println("snackid: " + jsonArray.getJSONObject(pq).getInt("snackid"));
                                        WormHole tempsnack = new WormHole(radius,  new LatLng(centerlat, centerlong));
                                        holes.add(tempsnack);

                                        CircleOptions temp = new CircleOptions()
                                                .center(new LatLng(tempsnack.center.x, tempsnack.center.y))
                                                .radius(tempsnack.radius)
                                                .strokeWidth(tempsnack.mWidth)
                                                .strokeColor(tempsnack.mStrokeColor)
                                                .fillColor(tempsnack.mFillColor);


                                        Circle temp1 = googleMap.addCircle(temp);
                                    }
                                    catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                }

                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //till here

                    String[] response = text.split("-");
                    //System.out.println("Text: "+text);
                    //System.out.println("len: "+text.length());
                    if (response[0].equals("success")) {
                        //    SharedPreferences pref;
                        //    SharedPreferences.Editor editor;
                        //    pref = getApplicationContext().getSharedPreferences("UserSession", 0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    } else {
                    /*    JSONObject json_data = new JSONObject(text);
                        int userId = Integer.parseInt(json_data.getString("snackid"));

                        System.out.println("json" + json_data.toString() + " Ahaa " + userId);
*/
                        Controller2.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Controller2.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {

                        reader.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();

    }


    public void createrepeller(){

        repeller = new RepellerBubble(gamemodel.isPlacedBubble,gamemodel,InitialLoc);

        Circle temp3 = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(repeller.center.x, repeller.center.y))
                .radius(repeller.radius)
                .strokeWidth(repeller.mWidth)
                .strokeColor(repeller.mStrokeColor)
                .fillColor(repeller.mFillColor));;
        repe.add(temp3);

    }

    public void CollisionPlayerNonplayer(){

        for(int i=0;i<snac.size();i++){

            double distanceBwBubbles = distFrom(snacks.get(i).center.x,snacks.get(i).center.y,Player.getCenter().latitude,Player.getCenter().longitude);

            Log.d("Snack"," and player");

            if(Player.getRadius() + snacks.get(i).radius >= Math.abs(distanceBwBubbles)){

                if(Player.getRadius() > snacks.get(i).radius){
                    double r1 = Player.getRadius();
                    double r2 = snacks.get(i).radius;

                    double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);
                    totalscore = totalscore + (int)( 30 * Math.abs(Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0)));
                    score.setText(" SCORE : " + totalscore);
                    Player.setRadius(newRadius);
                    snac.get(i).remove();
                    snac.remove(i);
                    snacks.remove(i);
                    break;
                }

                else{
                    Log.d("Snack"," and player!!..Game Over!!");

                    Player.remove();
                    gameover = true;
                }

            }
            if(gameover)
                break;
        }

        for(int i=0;i<jun.size();i++){

            double distanceBwBubbles = distFrom(junks.get(i).center.x,junks.get(i).center.y,Player.getCenter().latitude,Player.getCenter().longitude);

            if(Player.getRadius() + junks.get(i).radius >= Math.abs(distanceBwBubbles)){
                Log.d("Junk"," and player");
                if(Player.getRadius() > junks.get(i).radius){
                    double r1 = Player.getRadius();
                    double r2 = junks.get(i).radius;

                    double newRadius = Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);


                    totalscore = totalscore - (int)(30 * Math.abs(Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0)));
                    score.setText(" SCORE : " + totalscore);

                    Player.setRadius(newRadius);
                    jun.get(i).remove();
                    jun.remove(i);
                    junks.remove(i);
                    break;
                }

                else{
                    Log.d("Junk"," and player!!..Game Over!!");
                    Player.remove();
                    gameover = true;
                }
            }
            if(gameover)
                break;

        }

        for(int i=0;i<snac.size();i++){

            for(int j=0;j<otherPlayers.size();j++) {

                double distanceBwBubbles = distFrom(snacks.get(i).center.x, snacks.get(i).center.y, otherPlayers.get(j).getCenter().latitude, otherPlayers.get(j).getCenter().longitude);

                Log.d("Snack", " and player");

                if (otherPlayers.get(j).getRadius() + snacks.get(i).radius >= Math.abs(distanceBwBubbles)) {

                    if (otherPlayers.get(j).getRadius() > snacks.get(i).radius) {
                        double r1 = otherPlayers.get(j).getRadius();
                        double r2 = snacks.get(i).radius;

                        double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);
                        totalscore = totalscore + (int) (30 * Math.abs(Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0)));
                        score.setText(" SCORE : " + totalscore);
                        otherPlayers.get(j).setRadius(newRadius);
                        snac.get(i).remove();
                        snac.remove(i);
                        snacks.remove(i);
                        break;
                    } else {
                        double r1 = otherPlayers.get(j).getRadius();
                        double r2 = snacks.get(i).radius;

                        double newRadius = Math.pow((Math.pow(r2, 3) - Math.pow(r1, 3)), 1.0 / 3.0);
                        //totalscore = totalscore + (int) (30 * Math.abs(Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0)));
                        //score.setText(" SCORE : " + totalscore);
                        snacks.get(i).radius = newRadius;
                        snac.get(i).setRadius(newRadius);
                        Log.d("Snack", " and other 1 1 player!!..Game Over!!");
                        otherPlayers.get(j).remove();
                        otherPlayers.remove(j);
                        //gameover = true;
                    }

                }
                //if (gameover)
                //    break;
            }
           // if (gameover)
             //   break;
        }

        for(int i=0;i<jun.size();i++) {

            for (int j = 0; j < otherPlayers.size(); j++) {

                double distanceBwBubbles = distFrom(junks.get(i).center.x, junks.get(i).center.y, otherPlayers.get(j).getCenter().latitude, otherPlayers.get(j).getCenter().longitude);

                if (otherPlayers.get(j).getRadius() + junks.get(i).radius >= Math.abs(distanceBwBubbles)) {
                    Log.d("Junk", " and player");
                    if (otherPlayers.get(j).getRadius() > junks.get(i).radius) {
                        double r1 = otherPlayers.get(j).getRadius();
                        double r2 = junks.get(i).radius;

                        double newRadius = Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);


                        totalscore = totalscore - (int) (30 * Math.abs(Math.pow((Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0)));
                        score.setText(" SCORE : " + totalscore);

                        otherPlayers.get(j).setRadius(newRadius);
                        jun.get(i).remove();
                        jun.remove(i);
                        junks.remove(i);
                        break;
                    } else {
                        Log.d("Junk", " and other 1 1  player!!..Game Over!!");

                        double r1 = otherPlayers.get(j).getRadius();
                        double r2 = junks.get(i).radius;

                        double newRadius = Math.pow((Math.pow(r2, 3) - Math.pow(r1, 3)), 1.0 / 3.0);

                        junks.get(i).radius = newRadius;
                        jun.get(i).setRadius(newRadius);

                        otherPlayers.get(j).remove();
                        otherPlayers.remove(j);
                        //gameover = true;
                    }
                }
                //if (gameover)
                //    break;

            }

          //  if (gameover)
          //      break;

        }


    }

    public void CollisionWormHole(){

        Random ran = new Random();
        int ch = 0;

        for(int i = 0 ;i<holes.size(); i++){
            Log.d(" size of wormholes ", " " + holes.size());
            for(int j = 0;j<snacks.size(); j++){

                double distanceBw = distFrom(holes.get(i).center.x,holes.get(i).center.y,snacks.get(j).center.x,snacks.get(j).center.y);
                Log.d(" yoo "," snackkkkk and hole");

                if(snacks.get(j).radius + holes.get(i).radius >= Math.abs(distanceBw)){

                    int hole = ((ran.nextInt(holes.size()) + 0) % holes.size());

                    while (hole == i)
                        hole = ((ran.nextInt(holes.size()) + 0) % holes.size());

                    snacks.get(j).center.x = holes.get(hole).center.x;
                    snacks.get(j).center.y = holes.get(hole).center.y;

                    snac.get(j).setCenter(new LatLng(holes.get(hole).center.x,holes.get(hole).center.y));

                    while (distFrom(snacks.get(j).center.x, snacks.get(j).center.y, holes.get(hole).center.x, holes.get(hole).center.y) < snac.get(j).getRadius() + holes.get(hole).radius) {
                        // circles.get(i).circle.setCenter(new LatLng(circles.get(i).circle.getCenter().latitude + Math.sin(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed, circles.get(i).circle.getCenter().longitude + Math.cos(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed));
                        Log.d(" yoo "," snackkkkk and hole part2");
                        if(j>=snacks.size()){
                            break;
                        }

                        double locx = snacks.get(j).center.x + Math.sin(Math.toRadians(snacks.get(j).dir)) * snacks.get(j).speed;
                        double locy = snacks.get(j).center.y + Math.cos(Math.toRadians(snacks.get(j).dir)) * snacks.get(j).speed;;
                        snacks.get(j).center.x = locx;
                        snacks.get(j).center.y = locy;
                        snac.get(j).setCenter(new LatLng(locx,locy));


                    }
                }

            }


            for(int j = 0;j < junks.size(); j++){

                double distanceBw = distFrom(holes.get(i).center.x,holes.get(i).center.y,junks.get(j).center.x,junks.get(j).center.y);

                if(junks.get(j).radius + holes.get(i).radius >= Math.abs(distanceBw)){

                    Log.d(" yoo "," junkkkkk and hole");

                    int hole = ((ran.nextInt(holes.size()) + 0) % holes.size());

                    while (hole == i)
                        hole = ((ran.nextInt(holes.size()) + 0) % holes.size());

                    junks.get(j).center.x = holes.get(hole).center.x;
                    junks.get(j).center.y = holes.get(hole).center.y;

                    jun.get(j).setCenter(new LatLng(holes.get(hole).center.x,holes.get(hole).center.y));

                    while (distFrom(junks.get(j).center.x, junks.get(j).center.y, holes.get(hole).center.x, holes.get(hole).center.y) < jun.get(j).getRadius() + holes.get(hole).radius) {
                        // circles.get(i).circle.setCenter(new LatLng(circles.get(i).circle.getCenter().latitude + Math.sin(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed, circles.get(i).circle.getCenter().longitude + Math.cos(Math.toRadians(circles.get(i).direction)) * circles.get(i).speed));
                        Log.d(" yoo "," junkkkkk and hole part2");
                        if(j>=junks.size()){
                            break;
                        }
                        Log.d("junks size",junks.size() + " .. " );
                        double locx = junks.get(j).center.x + Math.sin(Math.toRadians(junks.get(j).dir)) * junks.get(j).speed;
                        double locy = junks.get(j).center.y + Math.cos(Math.toRadians(junks.get(j).dir)) * junks.get(j).speed;
                        junks.get(j).center.x = locx;
                        junks.get(j).center.y = locy;
                        jun.get(j).setCenter(new LatLng(locx,locy));


                    }
                }
                if(j>=junks.size()){
                    break;
                }

            }

        }

    }

    public void createboundary(String boundaryType){

        if (BoundaryType.equals("Large")) {

            Log.d("boundary is", "Large" + BoundaryType);

            repellerSafeDistance = 10.0;


            if(InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

        } else if (BoundaryType.equals("Medium")) {

            Log.d("boundary is", "medium" + BoundaryType);

            repellerSafeDistance = 8.0;

            if (InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.8f));

        } else {

            Log.d("boundary is", "small" + BoundaryType);

            repellerSafeDistance = 6.0;

            if (InitialLoc != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 20.8f));

        }


        googleMap.addPolygon(new PolygonOptions()
                .addAll(createRectangle(new LatLng(SourceLoc.latitude, SourceLoc.longitude), gamemodel.boundaryWidth / 2, gamemodel.boundaryHeight / 2))
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

            gps.showSettingsAlert(Controller2.this);
        }

    }

/*
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
*/

    @Override
    protected void onResume(){

        Log.d("controller on","resume");
        super.onResume();
    }

    @Override
    protected void onPause(){

        Log.d("controller on","pause");
        super.onPause();
    }

    @Override
    protected void onStop(){

        Log.d("controller on","stop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){

        Log.d("controller on","destroy");
        super.onDestroy();
    }


}
