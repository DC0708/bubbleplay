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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.model.LatLng;
import static com.example.mapdemo.CommonUtilities.EXTRA_MESSAGE;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 **/
public final class MainActivity extends AppCompatActivity {

    private RadioGroup radioBoundaryGroup;
    private RadioButton radioBoundaryButton;
    private Button btnDisplay;

    private MediaPlayer mediaPlayer;
    Toolbar toolbar;
    ImageButton FAB;
    ImageButton FAB1;
    ImageButton FAB2;
    ImageButton login;
    ImageButton register;
    ImageButton logout;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String username;
    LinearLayout linearLayout;
    LinearLayout lr,ul;
    TextView user;

    private LatLng InitialLoc;

    public String bestProvider = LocationManager.GPS_PROVIDER;
    public Boolean isGPSEnabled = false;
    public Location location;

    @Override
    protected void onStart(){

        Log.d("On :","Start!");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Main activity ","crested!!!!!");

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.main);

        user = (TextView) findViewById(R.id.username);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        user.setTypeface(custom_font);

        //linearLayout = (LinearLayout)findViewById(R.id.user_content);
        //linearLayout.setVisibility(View.INVISIBLE);

        mediaPlayer = MediaPlayer.create(this, R.raw.gamebubble);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //     toolbar = (Toolbar) findViewById(R.id.toolbar);
        //     setSupportActionBar(toolbar);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB1 = (ImageButton) findViewById(R.id.imageButton1);
        FAB2 = (ImageButton) findViewById(R.id.imageButton2);
        login = (ImageButton) findViewById(R.id.imageButton5);
        register = (ImageButton) findViewById(R.id.imageButton4);
        lr = (LinearLayout) findViewById(R.id.loginRegister);
        ul = (LinearLayout) findViewById(R.id.userLogout);
        logout = (ImageButton) findViewById(R.id.imageButton3);

        TextView tx = (TextView)findViewById(R.id.textView);
        tx.setTypeface(custom_font);

        SharedPreferences sp;

        TextView username = (TextView) findViewById(R.id.username);
        //TextView email = (TextView) findViewById(R.id.email);
        //ImageButton register = (ImageButton) findViewById(R.id.reg_button);
        //ImageButton login = (ImageButton) findViewById(R.id.login_button);

        sp = getApplicationContext().getSharedPreferences("UserSession", 0);

        if (sp.getBoolean("IsLoggedIn",false))
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            if(locationManager!=null) {

                Log.d("hererer", bestProvider + " ");

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                Log.d("hererer", isGPSEnabled + " ");

                bestProvider = locationManager.getBestProvider(criteria, true);
                Log.d("best provider", bestProvider + " ");
                if (bestProvider == null || location == null){

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    bestProvider = locationManager.GPS_PROVIDER;
                }

                while (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    bestProvider = locationManager.NETWORK_PROVIDER;
                }


                if (isGPSEnabled) {

//            String bestProvider = locationManager.getBestProvider(criteria, true);
//            location = locationManager.getLastKnownLocation(bestProvider);

                    if (location != null) {
                        //onLocationChanged()
                        InitialLoc = new LatLng(location.getLatitude(), location.getLongitude());

                        Log.d(" lat " + location.getLatitude(), " lon " + location.getLongitude());

                        if (InitialLoc != null) {
                            Log.d("user is : ",sp.getString("username","username") + " ");
                            sendpostrequest(InitialLoc, sp.getString("username","username"));
                            //String idds;
                            checkpush("Hi!..DC and CR7 are here!!");
                        }

                    } else {
                        Log.d("location error", "location is not enabled!!");

                    }
                    if (location != null) {
                        // onLocationChanged(location);
                        Log.d(" initial location set ", location.getLatitude() + " " + location.getLongitude());

                        Log.d("Location is if changed ", location.getLatitude() + " " + location.getLongitude());


                    }

                } else {
                    Log.d("Gps", "is not onn!!!");
                    //   gps.showSettingsAlert(Controller1.this);
                }


            }
            System.out.println("Logged in!");
            username.setText(sp.getString("username","username"));
           //email.setText(sp.getString("email","email"));
            lr.setVisibility(View.GONE);

        }
        else
        {
            System.out.println("not Logged in!");
            //username.setVisibility(View.GONE);
            //email.setVisibility(View.INVISIBLE);
            //login.setVisibility(View.VISIBLE);
            //register.setVisibility(View.VISIBLE);
            ul.setVisibility(View.GONE);
        }


        Log.d("hyugf", "fgdfgdrtsgrf");

        callbackManager = CallbackManager.Factory.create();
  //        setContentView(R.layout.main);
  //      info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.setReadPermissions("user_friends");

     //   loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {



//            @Override
//            public void onSuccess(final LoginResult loginResult) {
///*                info.setText(
//                        "User ID: "
//                                + loginResult.getAccessToken().getUserId()
//                                + "\n" +
//                                "Auth Token: "
//                                + loginResult.getAccessToken().getToken()
//                );
//*/              username = loginResult.getAccessToken().getToken();
//
//                ProfilePictureView profilePictureView;
//                profilePictureView = (ProfilePictureView) findViewById(R.id.ProfilePicture);
//                profilePictureView.setProfileId(loginResult.getAccessToken().getUserId());
//
//                linearLayout.setVisibility(View.VISIBLE);
//
//                new GraphRequest(
//                        AccessToken.getCurrentAccessToken(),
//                        "/me",
//                        null,
//                        HttpMethod.GET,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                 /* handle the result */
//                                try {
//
//                                    user.setText("Hi " + response.getJSONObject().get("name") + "!");
////                                    System.out.println(response.getJSONObject().get("name"));
//                                }
//                                catch(JSONException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                ).executeAsync();
//
//                new GraphRequest(
//                        AccessToken.getCurrentAccessToken(),
//                        "/me/friends",
//                        null,
//                        HttpMethod.GET,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                 /* handle the result */
//                                try {
//
//                                      //System.out.println(response);
//                                      System.out.println(response.getJSONObject());
//                                }
//                                catch(Exception e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                ).executeAsync();
//
// //               Log.d("fb status",);
//
//
//            }
//                @Override
//                public void onCancel () {
//    //                info.setText("Login attempt canceled.");
//                    Log.d("Cancel","sffds");
//                }
//
//                @Override
//                public void onError (FacebookException err){
//      //              info.setText("Login attempt failed.");
//                    Log.d("Error","sffds");
//                }
//
//      //      });



        FAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d("gjygyf","fhfdhtct");

 //               Intent myIntent = new Intent(MainActivity.this, TransitionPlayers.class);
  //              myIntent.putExtra("name",username);
  //              Toast.makeText(MainActivity.this,"Play button chosen",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                startActivity(new Intent(MainActivity.this, TransitionPlayers.class));
//                finish();
                return;
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }


        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }


        });

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Logout.class));
            }


        });

        Log.d("lol",FAB.hasOnClickListeners()+" ");

        FAB1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                //Toast.makeText(MainActivity.this,"About to be written",Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MainActivity.this, About.class));
 //               finish();
                return;
            }
        });

        FAB2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                //Toast.makeText(MainActivity.this,"About to be written",Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MainActivity.this, Instructions.class));
   //             finish();
                return;
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void checkpush( final String msg){

        new Thread(new Runnable() {
            public void run() {
                String [] ids = new String[1];
                ids[0] = "APA91bF8HLq-lp6Z7eVwQY6l8JLzd70CaKNOobna3ioqnPOooj-bAHYgjEa5Nchsqk5nt354jmmgYitEjcWMfW77lRFhle6fwUjWQyzcAOhDJ69-Z_BNeKsQyLVBUAPC5B7qRqS67e5T";
                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("regIds", "UTF-8")
                            + "=" + URLEncoder.encode(java.util.Arrays.toString(ids), "UTF-8");
                    data += "&" + URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode(msg , "UTF-8");
                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                BufferedReader reader=null;

                // Send data
                try
                {
                    // Defined URL  where to send data
                    URL url = new URL("http://10.1.35.160/BubblePlayServer/send_message.php");

                    // Send POST data request
                    Log.d("its pushh:","notification !!");
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
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        System.out.println(builder);
                        System.out.println(builder.length());
                        //sb.append(line + "\n");
                        builder+=line;

                    }


                    final String text = builder;
                    System.out.println("Text: "+text);
                    System.out.println("len: "+text.length());
                    if (text.equals("success"))
                    {
                        Log.d("updated location","yoo!!");
                        /*    SharedPreferences pref;
                            SharedPreferences.Editor editor;
                          .  pref = getApplicationContext()getSharedPreferences("UserSession",0);
                            editor = pref.edit();
                            editor.putBoolean("IsLoggedIn",true);
                            editor.putString("username",user);
                            editor.putString("email",email.getText().toString());
                            editor.commit();

                            startActivity(new Intent(Register.this, MainActivity.class));
                        */
                    }
                    else
                    {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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

    public void sendpostrequest(final LatLng initialLoc,final String user){


            new Thread(new Runnable() {
                public void run() {



                    // Get user defined values
                    //System.out.println("Trying to connect!");
                    String data = "";
                    // Create data variable for sent values to server
                    try {

                        data = URLEncoder.encode("latitude", "UTF-8")
                                + "=" + URLEncoder.encode(String.valueOf(initialLoc.latitude), "UTF-8");

                        data += "&" + URLEncoder.encode("longitude", "UTF-8") + "="
                                + URLEncoder.encode(String.valueOf(initialLoc.longitude), "UTF-8");

                        data += "&" + URLEncoder.encode("username", "UTF-8")
                                + "=" + URLEncoder.encode(user, "UTF-8");
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
                        URL url = new URL("http://10.1.35.160/BubblePlayServer/updateiniloc.php");

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
                        System.out.println("Text: "+text);
                        System.out.println("len: "+text.length());
                        if (text.equals("success"))
                        {
                            Log.d("updated location","yoo!!");
                        /*    SharedPreferences pref;
                            SharedPreferences.Editor editor;
                          .  pref = getApplicationContext()getSharedPreferences("UserSession",0);
                            editor = pref.edit();
                            editor.putBoolean("IsLoggedIn",true);
                            editor.putString("username",user);
                            editor.putString("email",email.getText().toString());
                            editor.commit();

                            startActivity(new Intent(Register.this, MainActivity.class));
                        */
                        }
                        else
                        {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.id.menu_legal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_legal) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    *
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
/*    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.

        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        RadioGroup radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioBoundaryGroup.setVisibility(View.INVISIBLE);
        Button button = (Button) findViewById(R.id.btnDisplay);
        button.setVisibility(View.INVISIBLE);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setEmptyView(findViewById(R.id.empty));

      //  addListenerOnButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal ) {


         /*   btnDisplay = (Button) findViewById(R.id.btnDisplay);

            btnDisplay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // get selected radio button from radioGroup
                    int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioBoundaryButton = (RadioButton) findViewById(selectedId);

                 }


            }
        startActivity(new Intent(this, LegalInfoActivity.class));
        return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        Log.d("aalo111","yoo111");

        btnDisplay.setVisibility(View.VISIBLE);
        radioBoundaryGroup.setVisibility(View.VISIBLE);

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();
                Log.d("aalo","yo00o");

//                startActivity(new Intent(MainActivity.this, LegalInfoActivity.class));
                Log.d("aalo","yo1o");
                Intent i = new Intent(MainActivity.this, demo.activityClass);
                String strName = null;
                i.putExtra("Boundary", radioBoundaryButton.getText());
                //            DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);
//                startActivity(new Intent(MainActivity.this, TopView.class));
                startActivity(i);
                //startActivity(new Intent(MainActivity.this, MarkerDemoActivity.class));

                Log.d("aalo","yo2o");

                //return true;

            }

        });



    }

    public void addListenerOnButton() {

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        Log.d("aalo111","yoo111");

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();

            }

        });

    }

*/
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
//            lblMessage.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){

        super.onPause();
    }

    @Override
    protected void onStop(){

        Log.d("On :","Stop!!");
        super.onStop();
    }

    @Override
    protected void onDestroy(){

        Log.d("On :","Destroy!");
        super.onDestroy();
    }




}
