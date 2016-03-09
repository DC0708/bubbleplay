package com.example.mapdemo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class Timer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        System.out.println("ENtered timer Activity");

        //ArrayList<PlayerDetails> chosenPlayers = (ArrayList<PlayerDetails>) getIntent().

        TextView tv = (TextView) findViewById(R.id.textView);
        String chosenOnes = getIntent().getExtras().getString("chosenPlayers");
        String deviceids = getIntent().getExtras().getString("appID");

        checkpush("Challenge is here!!",deviceids);

        System.out.println(chosenOnes);

        tv.setText(chosenOnes);
    }


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


}
