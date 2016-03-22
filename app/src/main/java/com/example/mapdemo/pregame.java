package com.example.mapdemo;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
    public String challengeid;
    private LatLng InitialLoc;


    Intent in ;

    public String BoundaryType;
    double playerradii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);
        in  = new Intent(pregame.this,Controller2.class);
        String accepted = "";

        String challenger_chk = "";

        final TextView names = (TextView) findViewById(R.id.names);
        final Bundle extr = getIntent().getExtras();
        if (extr.containsKey("challengeid"))
        {
            challenger_chk="yes";
            accepted = getIntent().getExtras().getString("accepted");
            names.setText(accepted);
        }
        else
        {
            names.setText("");
        }

        final TextView tim = (TextView) findViewById(R.id.tim);

           //tim.setText(getIntent().getExtras().getString("challengeID"));




        new CountDownTimer(10000, 1000) {


            public void onTick(long millisUntilFinished) {

                tim.setText("Game will start in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Toast.makeText(pregame.this, "Start the game!", Toast.LENGTH_SHORT).show();
                if(extr.containsKey("accepted"))
                    in.putExtra("challengeid",extr.getString("accepted"));
                else {
                    in.putExtra("challengeid", extr.getString("challengeid"));
                }
                startActivity(in);

            }

        }.start();

        if(challenger_chk.equals("yes")){

            final Bundle extras = getIntent().getExtras();



            if (extras != null) {
                BoundaryType = "Large";//extras.getString("Boundary");
                Gamemode = extras.getString("gamemode");
                Playermode = extras.getString("playermode");
                challengeid = extras.getString("challengeid");
                if(extras.containsKey("accepted"))
                challengeid = extras.getString("accepted");
            }
            getSourceLoc(challengeid);
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



    }

    public void createsnackbubbles(){

        for(int i=0;i<gamemodel.sizeSnackBubble;i++){
            createsnack(i);

        }
    }
    public void createsnack(int i){

        SnackBubble tempsnack = new SnackBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc, playerradii);
        initialsnacks.add(tempsnack);
        final int index = i;

        new Thread(new Runnable() {
            public void run() {

                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(challengeid, "UTF-8");

                    data += "&" + URLEncoder.encode("radius", "UTF-8") + "="
                            + URLEncoder.encode(String.valueOf(initialsnacks.get(index).radius), "UTF-8");

                    data += "&" + URLEncoder.encode("direction", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialsnacks.get(index).dir), "UTF-8");

                    data += "&" + URLEncoder.encode("lat", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialsnacks.get(index).center.x), "UTF-8");

                    data += "&" + URLEncoder.encode("longi", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialsnacks.get(index).center.y), "UTF-8");

                    data += "&" + URLEncoder.encode("speed", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialsnacks.get(index).speed), "UTF-8");

                }
                catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                BufferedReader reader=null;

                // Send data
                try
                {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL +"snackupdate.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder+=line;

                    }


                    final String text = builder;
                    String[] response = text.split("-");
                    System.out.println("Text: "+text);
                    System.out.println("len: "+text.length());
                    if (response[0].equals("success"))
                    {


                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        pref = getApplicationContext().getSharedPreferences("UserSession",0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    }
                    else
                    {
                        pregame.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(pregame.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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

                        reader.close();
                    }

                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();



    }

    public void getSourceLoc(final String Challengeid){


        new Thread(new Runnable() {
            public void run(){


                String data1 = "";
                // Create data variable for sent values to server
                try {

                    data1 += "&" + URLEncoder.encode("challengeid", "UTF-8")
                            + "=" + URLEncoder.encode(Challengeid, "UTF-8");
                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                BufferedReader reader1=null;

                // Send data
                try
                {
                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "fetchsourcelocation.php");

                    // Send POST data request
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
                    final JSONArray jsonArray = new JSONArray(text);
                    System.out.println("total: " + jsonArray.length());

                    for (int pq = 0; pq < jsonArray.length(); pq++) {
                        try {
                            double centerlat = jsonArray.getJSONObject(pq).getDouble("latitude");

                            double centerlong = jsonArray.getJSONObject(pq).getDouble("longitude");

                            in.putExtra("sourcelat",centerlat);

                            in.putExtra("sourcelong",centerlong);

                            System.out.println("All players location updated");
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }

                    }

                    System.out.println("Textii: "+text);
                    System.out.println("len: "+text.length());
                    if (!text.equals("failure"))
                    {
                            pregame.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(pregame.this, "location in Controller2 " + text, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        pregame.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(pregame.this, "Error22: " + text, Toast.LENGTH_SHORT).show();
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


    public void createjunkbubbles(){

        for(int i=0;i<gamemodel.sizeJunkBubbles;i++){
            createjunk(i);
        }



    }
    public void createjunk(int i){

        JunkBubble tempjunk = new JunkBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc,playerradii);
        initialjunks.add(tempjunk);
        final int index = i;

        new Thread(new Runnable() {
            public void run() {



                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                            + URLEncoder.encode(challengeid, "UTF-8");

                    data += "&" + URLEncoder.encode("radius", "UTF-8") + "="
                            + URLEncoder.encode(String.valueOf(initialjunks.get(index).radius), "UTF-8");

                    data += "&" + URLEncoder.encode("direction", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialjunks.get(index).dir), "UTF-8");

                    data += "&" + URLEncoder.encode("lat", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialjunks.get(index).center.x), "UTF-8");

                    data += "&" + URLEncoder.encode("longi", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialjunks.get(index).center.y), "UTF-8");

                    data += "&" + URLEncoder.encode("speed", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(initialjunks.get(index).speed), "UTF-8");

                }
                catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

                BufferedReader reader=null;

                // Send data
                try
                {

                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL +"junkupdate.php");

                    // Send POST data request

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    // Get the server response

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    String builder = "";

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder+=line;

                    }


                    final String text = builder;
                    String[] response = text.split("-");
                    System.out.println("Text: "+text);
                    System.out.println("len: "+text.length());
                    if (response[0].equals("success"))
                    {


                        SharedPreferences pref;
                        SharedPreferences.Editor editor;
                        pref = getApplicationContext().getSharedPreferences("UserSession",0);
                        System.out.println(response[0].toString());
                        System.out.println("Logging in!");
                    }
                    else
                    {
                        pregame.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(pregame.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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

                        reader.close();
                    }

                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }).start();




    }

    public void createwormholes(){

        for(int i=0;i<gamemodel.sizeWormHoles;i++){
            WormHole temphole = new WormHole(gamemodel.xCoordinate,gamemodel.yCoordinate,i,gamemodel,InitialLoc);
            initialholes.add(temphole);

            final int index = i;

            new Thread(new Runnable() {
                public void run() {



                    // Get user defined values
                    //System.out.println("Trying to connect!");
                    String data = "";
                    // Create data variable for sent values to server
                    try {

                        data += "&" + URLEncoder.encode("challengeid", "UTF-8") + "="
                                + URLEncoder.encode(challengeid, "UTF-8");

                        data += "&" + URLEncoder.encode("radius", "UTF-8") + "="
                                + URLEncoder.encode(String.valueOf(initialholes.get(index).radius), "UTF-8");

                        data += "&" + URLEncoder.encode("lat", "UTF-8")
                                + "=" + URLEncoder.encode(String.valueOf(initialholes.get(index).center.x), "UTF-8");

                        data += "&" + URLEncoder.encode("longi", "UTF-8")
                                + "=" + URLEncoder.encode(String.valueOf(initialholes.get(index).center.y), "UTF-8");

                    }
                    catch(UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    BufferedReader reader=null;

                    // Send data
                    try
                    {

                        // Defined URL  where to send data
                        URL url = new URL(CommonUtilities.SERVER_URL +"holeupdate.php");

                        // Send POST data request

                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write( data );
                        wr.flush();

                        // Get the server response

                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        String builder = "";

                        // Read Server Response
                        while((line = reader.readLine()) != null)
                        {
                            // Append server response in string
                            System.out.println(builder);
                            System.out.println(builder.length());
                            //sb.append(line + "\n");
                            builder+=line;

                        }


                        final String text = builder;
                        String[] response = text.split("-");
                        System.out.println("Text: "+text);
                        System.out.println("len: "+text.length());
                        if (response[0].equals("success"))
                        {


                            SharedPreferences pref;
                            SharedPreferences.Editor editor;
                            pref = getApplicationContext().getSharedPreferences("UserSession",0);
                            System.out.println(response[0].toString());
                            System.out.println("Logging in!");
                        }
                        else
                        {
                            pregame.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(pregame.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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

                            reader.close();
                        }

                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }).start();

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
