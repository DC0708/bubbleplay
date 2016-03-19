package com.example.mapdemo;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Player;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class pregame extends ActionBarActivity {

    public Model gamemodel;

    public List<SnackBubble> initialsnacks = new ArrayList<SnackBubble>(20);

    public List<JunkBubble> initialjunks = new ArrayList<JunkBubble>(20);

    private static final double DEFAULT_RADIUS = 0.5;

    public List<WormHole> initialholes = new ArrayList<WormHole>(20);

    public String Gamemode;

    public Location location;

    public final double mediumfactor = 1.5;
    public final double largefactor = 2.0;

    public Boolean isGPSEnabled = false;

    public String bestProvider = LocationManager.GPS_PROVIDER;

    GeolocationService gps;

    public String Playermode;
    public int challenger=0;

    private LatLng InitialLoc;

    public String BoundaryType;
    double playerradii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        String accepted = "";

        final TextView names = (TextView) findViewById(R.id.names);
        final Bundle extr = getIntent().getExtras();
        if (extr.containsKey("accepted"))
        {
            challenger=1;
            accepted = getIntent().getExtras().getString("accepted");
            names.setText(accepted);
        }
        else
        {
            names.setText("");
        }

        final TextView tim = (TextView) findViewById(R.id.tim);;

           //tim.setText(getIntent().getExtras().getString("challengeID"));



        new CountDownTimer(10000, 1000) {

/*            if( challenger==1 ){

                final Bundle extras = getIntent().getExtras();



                if (extras != null) {
                    BoundaryType = extras.getString("Boundary");
                    Gamemode = extras.getString("gamemode");
                    Playermode = extras.getString("playermode");
                }

                playerradii = DEFAULT_RADIUS;
                if(BoundaryType.equals("Small")){
                    playerradii = DEFAULT_RADIUS;
                }
                else if(BoundaryType.equals("Medium")){
                    playerradii = mediumfactor * DEFAULT_RADIUS;
                }
                else{
                    playerradii = largefactor * DEFAULT_RADIUS;
                }
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                // Getting GPS status
                if (locationManager != null) {

                    isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (!isGPSEnabled) {
                        //                Log.d("in GPS settings","show settings");
                        gps.showSettingsAlert(pregame.this);
                    }

                    bestProvider = locationManager.getBestProvider(criteria, true);

                    //Log.d("hererer",location+" ");
                    location = locationManager.getLastKnownLocation(bestProvider);

                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        bestProvider = locationManager.GPS_PROVIDER;
                    }

                    while (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        bestProvider = locationManager.NETWORK_PROVIDER;
                    }

                    if (isGPSEnabled && location != null) {

                        InitialLoc = new LatLng(location.getLatitude(), location.getLongitude());

                    }

                    gamemodel = new Model(BoundaryType);

                    createsnackbubbles();
                    createjunkbubbles();
                    createwormholes();


                }

            }
*/
            public void onTick(long millisUntilFinished) {
                tim.setText("Game will start in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Toast.makeText(pregame.this, "Start the game!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(pregame.this,MainActivity.class));


            }

        }.start();


    }

    public void createsnackbubbles(){

        for(int i=0;i<gamemodel.sizeSnackBubble;i++){
            createsnack(i);

        }
    }
    public void createsnack(int i){

        SnackBubble tempsnack = new SnackBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc, playerradii);
        initialsnacks.add(tempsnack);


    }

    public void createjunkbubbles(){

        for(int i=0;i<gamemodel.sizeJunkBubbles;i++){
            createjunk(i);
        }
    }
    public void createjunk(int i){

        JunkBubble tempjunk = new JunkBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc,playerradii);
        initialjunks.add(tempjunk);


    }

    public void createwormholes(){

        for(int i=0;i<gamemodel.sizeWormHoles;i++){
            WormHole temphole = new WormHole(gamemodel.xCoordinate,gamemodel.yCoordinate,i,gamemodel,InitialLoc);
            initialholes.add(temphole);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pregame, menu);
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
