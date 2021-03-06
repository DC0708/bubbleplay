package com.example.mapdemo;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.games.Player;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static com.example.mapdemo.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.mapdemo.CommonUtilities.EXTRA_MESSAGE;
import static com.example.mapdemo.CommonUtilities.SENDER_ID;


public class Timer extends AppCompatActivity {
    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;

    String challengeID = "";

    String a = "";

    String b = "";

    String c = "";

    String d = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/GoodDog.otf");

        final TextView timer = (TextView) findViewById(R.id.timer);

        final TextView wait = (TextView) findViewById(R.id.textView9);
        wait.setTypeface(custom_font);

        final TextView tv = (TextView) findViewById(R.id.clock);

        System.out.println("Entered timer Activity");

        cd = new ConnectionDetector(getApplicationContext());

        final double insert_time = System.currentTimeMillis();

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Timer.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }


        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        //  lblMessage = (TextView) findViewById(R.id.lblMessage);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        String regId1="";
        //while(regId1.length()==0)
        regId1 = GCMRegistrar.getRegistrationId(this);

        final String regId = regId1;
        GCMRegistrar.register(this, SENDER_ID);

        //ArrayList<PlayerDetails> chosenPlayers = (ArrayList<PlayerDetails>) getIntent().

        //TextView tv = (TextView) findViewById(R.id.textView9);
        final String chosenOnes = getIntent().getExtras().getString("chosenPlayers");
        final String deviceids = getIntent().getExtras().getString("appID");

        System.out.println("Thread de uttteeeeee!!!");

        final ProgressDialog dialog = new ProgressDialog(Timer.this); // this = YourActivity

        Thread t = new Thread(new Runnable() {
            public void run(){

                Timer.this.runOnUiThread(new Runnable() {
                    public void run() {

                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Loading. Please wait...");
                        dialog.setIndeterminate(true);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                });

                System.out.println("Thread de andar !!!!");

                if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
                    // Skips registration.
                    Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
                } else {
                    // Try to register again, but not in the UI thread.
                    // It's also necessary to cancel the thread onDestroy(),
                    // hence the use of AsyncTask instead of a raw thread.
                    //final Context context = this;
                    mRegisterTask = new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            // Register on our server
                            Log.d("aaloo33", "bukhaara33");
                            // On server creates a new user
                            //  ServerUtilities.register(context, name, email, regId);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            Log.d("aaloo", "bukhaara");
                            mRegisterTask = null;
                            Log.d("aaloo11", "bukhaara22");
                        }

                    };
                    mRegisterTask.execute(null, null, null);
                }

                String [] ids = deviceids.split(",");
                Log.d(" devices are", ids[0]);



                String data1 = "";

                SharedPreferences sp = getApplicationContext().getSharedPreferences("UserSession",0);


                // Create data variable for sent values to server
                try {

                    data1 += "&" + URLEncoder.encode("name", "UTF-8")
                            + "=" + URLEncoder.encode(sp.getString("username","username"), "UTF-8");

                    data1 += "&" + URLEncoder.encode("chosenOnes", "UTF-8")
                            + "=" + URLEncoder.encode(chosenOnes, "UTF-8");

                    data1 += "&" + URLEncoder.encode("timestamp", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(insert_time), "UTF-8");




                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                BufferedReader reader1=null;

                try
                {
                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "insert_challenge.php");

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


                    challengeID = builder;
                    System.out.println("Chaallgeid: "+challengeID);
                    System.out.println("len: "+challengeID.length());


                    SharedPreferences.Editor edit;
                    edit = sp.edit();
                    edit.putString("challengeID",challengeID);
                    edit.commit();

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


                //ids[0] = "APA91bF8HLq-lp6Z7eVwQY6l8JLzd70CaKNOobna3ioqnPOooj-bAHYgjEa5Nchsqk5nt354jmmgYitEjcWMfW77lRFhle6fwUjWQyzcAOhDJ69-Z_BNeKsQyLVBUAPC5B7qRqS67e5T";
                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("regId", "UTF-8")
                            + "=" + URLEncoder.encode(java.util.Arrays.toString(ids), "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8")
                            + "=" + URLEncoder.encode(sp.getString("username","username"), "UTF-8");
                    data += "&" + URLEncoder.encode("challengeID", "UTF-8")
                            + "=" + URLEncoder.encode(challengeID, "UTF-8");
                    data += "&" + URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode("I am fucking tired!!!" , "UTF-8");
                }
                catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                BufferedReader reader=null;

                // Send data
                try
                {
                    // Defined URL  where to send data
                    URL url = new URL(CommonUtilities.SERVER_URL + "send_message.php");

                    // Send POST data request
                    Log.d("its pushh:", "notification !!");
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

                       /*   SharedPreferences pref;
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
                        Timer.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Timer.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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


                Timer.this.runOnUiThread(new Runnable() {
                    public void run() {

                        dialog.dismiss();
                    }
                });








            }

        });

        t.start();
        try {
            t.join();

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //dialog.dismiss();

//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
//        animation.setDuration (60000); //in milliseconds
//        animation.setInterpolator (new DecelerateInterpolator());
//        animation.start ();
//
//                new CountDownTimer(60000, 1000) {
//
//                    public void onTick(long millisUntilFinished) {
//                        System.out.println("seconds remaining: " + millisUntilFinished / 1000);
//                        //timer.setText("seconds remaining: " + millisUntilFinished / 1000);
//                        if ((millisUntilFinished/1000)==55)
//                        {
//                            new Thread(new Runnable() {
//                                public void run() {
//
//
//
//                                    String data2 = "";
//                                    // Create data variable for sent values to server
//                                    try {
//
//                                        data2 += "&" + URLEncoder.encode("challengeID", "UTF-8")
//                                                + "=" + URLEncoder.encode(challengeID, "UTF-8");
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    BufferedReader reader2 = null;
//
//                                    // Send data
//                                    try {
//                                        // Defined URL  where to send data
//                                        URL url = new URL(CommonUtilities.SERVER_URL + "pre_game_screen.php");
//
//                                        // Send POST data request
//                                        Log.d("its pushh:", "notification !!");
//                                        URLConnection conn = url.openConnection();
//                                        conn.setDoOutput(true);
//                                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                                        wr.write(data2);
//                                        wr.flush();
//
//                                        // Get the server response
//
//                                        reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                                        StringBuilder sb = new StringBuilder();
//                                        String line = null;
//                                        String builder = "";
//
//                                        // Read Server Response
//                                        while ((line = reader2.readLine()) != null) {
//                                            // Append server response in string
//                                            System.out.println(builder);
//                                            System.out.println(builder.length());
//                                            //sb.append(line + "\n");
//                                            builder += line;
//
//                                        }
//
//
//                                        final String text = builder;
//                                        System.out.println("Texter: " + text);
//                                        System.out.println("len: " + text.length());
//                                        if (!text.equals("failure")) {
//
//                                            inte.putExtra("accepted", challengeID);
//                                            inte.putExtra("challengeid",challengeID);
//                                            inte.putExtra("Boundary", getIntent().getExtras().getString("Boundary"));
//                                            inte.putExtra("gamemode", getIntent().getExtras().getString("gamemode"));
//                                            inte.putExtra("playermode", getIntent().getExtras().getString("playermode"));
//
//
//                                        } else {
//                                            Timer.this.runOnUiThread(new Runnable() {
//                                                public void run() {
//                                                    Toast.makeText(Timer.this, "Error: " + text, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//                                    } catch (Exception ex) {
//                                        ex.printStackTrace();
//                                    } finally {
//                                        try {
//
//                                            reader2.close();
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//
//                                }
//                            }).start();
//
//                        }
//                    }
//
//                    public void onFinish() {
//                        startActivity(inte);
//                          }
//                }.start();
        final java.util.Timer timer1 = new java.util.Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            int check=0;

            @Override
            public void run(){

                if ((System.currentTimeMillis()-insert_time)%1000.0 == 0.0) {
                    Timer.this.runOnUiThread(new Runnable() {
                        public void run() {
                            tv.setText(String.valueOf((System.currentTimeMillis() - insert_time) / 1000.0));
                        }
                    });


                }
                if (System.currentTimeMillis()>55000.0+insert_time && check==0)
                {

                    check=1;
                    new Thread(new Runnable() {
                                public void run() {



                                    String data2 = "";
                                    // Create data variable for sent values to server
                                    try {

                                        data2 += "&" + URLEncoder.encode("challengeID", "UTF-8")
                                                + "=" + URLEncoder.encode(challengeID, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    BufferedReader reader2 = null;

                                    // Send data
                                    try {
                                        // Defined URL  where to send data
                                        URL url = new URL(CommonUtilities.SERVER_URL + "pre_game_screen.php");

                                        // Send POST data request
                                        Log.d("its pushh:", "notification !!");
                                        URLConnection conn = url.openConnection();
                                        conn.setDoOutput(true);
                                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                                        wr.write(data2);
                                        wr.flush();

                                        // Get the server response

                                        reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        StringBuilder sb = new StringBuilder();
                                        String line = null;
                                        String builder = "";

                                        // Read Server Response
                                        while ((line = reader2.readLine()) != null) {
                                            // Append server response in string
                                            System.out.println(builder);
                                            System.out.println(builder.length());
                                            //sb.append(line + "\n");
                                            builder += line;

                                        }


                                        final String text = builder;
                                        System.out.println("Texter: " + text);
                                        System.out.println("len: " + text.length());
                                        if (!text.equals("failure")) {

                                            a = challengeID;
                                            b = getIntent().getExtras().getString("Boundary");
                                            c = getIntent().getExtras().getString("gamemode");
                                            d = getIntent().getExtras().getString("playermode");
//                                            inte.putExtra("accepted", challengeID);
//                                            inte.putExtra("challengeid",challengeID);
//                                            inte.putExtra("Boundary", getIntent().getExtras().getString("Boundary"));
//                                            inte.putExtra("gamemode", getIntent().getExtras().getString("gamemode"));
//                                            inte.putExtra("playermode", getIntent().getExtras().getString("playermode"));


                                        } else {
                                            Timer.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(Timer.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    } finally {
                                        try {

                                            reader2.close();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }

                                }
                            }).start();

                }
                if (System.currentTimeMillis()>60000.0+insert_time)
                {
                    timer1.purge();
                    timer1.cancel();
                    Intent inte = new Intent(Timer.this,pregame.class);
                    inte.putExtra("accepted", a);
                    inte.putExtra("challengeid",a);
                    inte.putExtra("Boundary", b);
                    inte.putExtra("gamemode", c);
                    inte.putExtra("playermode", d);
                    inte.putExtra("insert_time",insert_time);
                    startActivity(inte);
                }

            }
        }, 0, 1);



        System.out.println("Thread de thalllllleeeee");

        System.out.println(chosenOnes);

        //tv.setText(chosenOnes);
    }

/*
    public void createsnackbubbles(){

        for(int i=0;i<gamemodel.sizeSnackBubble;i++){
            createsnack(i);

        }
    }
    public void createsnack(int i){

        SnackBubble tempsnack = new SnackBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc, Player.getRadius());
        initialsnacks.add(tempsnack);

        CircleOptions temp = new CircleOptions()
                .center(new LatLng(tempsnack.center.x,tempsnack.center.y))
                .radius(tempsnack.radius)
                .strokeWidth(tempsnack.mWidth)
                .strokeColor(tempsnack.mStrokeColor)
                .fillColor(tempsnack.mFillColor);

    }

    public void createjunkbubbles(){

        for(int i=0;i<gamemodel.sizeJunkBubbles;i++){
            createjunk(i);
        }
    }
    public void createjunk(int i){

        JunkBubble tempjunk = new JunkBubble(gamemodel.isPlacedBubble,i,gamemodel,InitialLoc,Player.getRadius());
        initialjunks.add(tempjunk);


    }

    public void createwormholes(){

        for(int i=0;i<gamemodel.sizeWormHoles;i++){
            WormHole temphole = new WormHole(gamemodel.xCoordinate,gamemodel.yCoordinate,i,gamemodel,InitialLoc);
            initialholes.add(temphole);
        }
    }

*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
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

    public void checkpush(final String msg,final String devices){

        new Thread(new Runnable() {
            public void run() {
                String [] ids = devices.split(",");
                Log.d(" devices are", ids[0]);
                //ids[0] = "APA91bF8HLq-lp6Z7eVwQY6l8JLzd70CaKNOobna3ioqnPOooj-bAHYgjEa5Nchsqk5nt354jmmgYitEjcWMfW77lRFhle6fwUjWQyzcAOhDJ69-Z_BNeKsQyLVBUAPC5B7qRqS67e5T";
                // Get user defined values
                //System.out.println("Trying to connect!");
                String data = "";
                // Create data variable for sent values to server
                try {

                    data += "&" + URLEncoder.encode("regId", "UTF-8")
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
                    URL url = new URL("http://10.1.33.78/BubblePlayServer/send_message.php");

                    // Send POST data request
                    Log.d("its pushh:", "notification !!");
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

                       /*   SharedPreferences pref;
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
                        Timer.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Timer.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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
            Log.d("message ", " is " + newMessage);
//            lblMessage.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }


}
