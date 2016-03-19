package com.example.mapdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mapdemo.CommonUtilities;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.TimerTask;



public class Challenge extends ActionBarActivity {

    final java.util.Timer timer1 = new java.util.Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        TextView tx = (TextView)findViewById(R.id.textView8);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        ImageButton accept = (ImageButton) findViewById(R.id.accept_button);
        ImageButton reject = (ImageButton) findViewById(R.id.reject_button);

        final SharedPreferences shpr = getApplicationContext().getSharedPreferences("UserSession",0);

        final String challengeID = getIntent().getExtras().getString("challengeID");

        final TextView timer = (TextView) findViewById(R.id.timer);


        timer.setTypeface(custom_font);




        accept.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {


            Thread t = new Thread(new Runnable() {
                    public void run(){


                        String data = "";
                        // Create data variable for sent values to server
                        try {

                            data += "&" + URLEncoder.encode("challengeID", "UTF-8")
                                    + "=" + URLEncoder.encode(challengeID, "UTF-8");
                            data += "&" + URLEncoder.encode("name", "UTF-8")
                                    + "=" + URLEncoder.encode(shpr.getString("username","username") , "UTF-8");
                        }
                        catch(UnsupportedEncodingException e){
                            e.printStackTrace();
                        }

                        BufferedReader reader=null;

                        // Send data
                        try
                        {
                            // Defined URL  where to send data
                            URL url = new URL(CommonUtilities.SERVER_URL + "accept_challenge.php");

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
                                */


                                Challenge.this.runOnUiThread(new Runnable() {
                                    public void run() {

                                Toast.makeText(Challenge.this, "Challenge Accepted! Start the game", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Challenge.this, MainActivity.class));
                                    }
                                });

                            }
                            else
                            {
                                Challenge.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Challenge.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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



            });

                Thread t2 = new Thread(new Runnable() {
                    public void run(){


                        String data1 = "";
                        // Create data variable for sent values to server
                        try {

                            data1 += "&" + URLEncoder.encode("challengeID", "UTF-8")
                                    + "=" + URLEncoder.encode(challengeID, "UTF-8");
                        }
                        catch(UnsupportedEncodingException e){
                            e.printStackTrace();
                        }

                        BufferedReader reader1=null;

                        // Send data
                        try
                        {
                            // Defined URL  where to send data
                            URL url = new URL("http://10.1.33.78/BubblePlayServer/get_timer_time_left.php");

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


                                        //int millisInFuture = Integer.parseInt(text);
                                        final long millisInFuture = Long.parseLong(text);
                                        System.out.println("Time left: " + millisInFuture);

//                                Challenge.this.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        new CountDownTimer(millisInFuture, 1000) {
//
//                                            public void onTick(long millisUntilFinished) {
//                                                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
//                                            }
//
//                                            public void onFinish() {
//
//                                                Intent inte = new Intent(Challenge.this,pregame.class);
//                                                inte.putExtra("accepted",challengeID);
//                                                startActivity(inte);
//
//                                            }
//                                        };
//                                    }
//                                });
                                        class RemindTask extends TimerTask {
                                            public void run() {
                                                System.out.println("Time's up!");
                                                //timer.setText();
                                                timer1.cancel(); //Terminate the timer thread
                                                Intent inte = new Intent(Challenge.this,pregame.class);
                                                inte.putExtra("accepted",challengeID);
                                                startActivity(inte);
                                            }
                                        }

                                        timer1.schedule(new RemindTask(), millisInFuture*1000);








                            }
                            else
                            {
                                Challenge.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Challenge.this, "Error22: " + text, Toast.LENGTH_SHORT).show();
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



                });


                t.start();

                t2.start();
                try {
                    t.join();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }





                //               finish();
                return;

            }
        });

        reject.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Toast.makeText(Challenge.this, "Challenge Rejected!", Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(Challenge.this, MainActivity.class);

                myIntent.putExtra("playermode","single");

                startActivity(myIntent);

                return;
            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge, menu);
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


