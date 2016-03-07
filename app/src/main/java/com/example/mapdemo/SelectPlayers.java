package com.example.mapdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SelectPlayers extends ActionBarActivity {

    ArrayList<PlayerDetails> players = new ArrayList<PlayerDetails>();
    ListAdapter boxAdapter;
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_players);

        sp  = getApplicationContext().getSharedPreferences("UserSession",0);

        if (sp.getBoolean("isLoggedIn",false)) {
            startActivity(new Intent(SelectPlayers.this, Login.class));
        }
        else
        {
            new Thread(new Runnable() {
                public void run() {

                    String data = "";
                    // Create data variable for sent values to server
                    try {

                        data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                                + URLEncoder.encode(sp.getString("email","email"), "UTF-8");


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
                        //Log.d("yoo:", "in try for register!" + username.getText().toString() +
                          //      email.getText().toString() + password.getText().toString());
                        URL url = new URL("http://192.168.1.6/BubblePlayServer/getAppID.php");

                        // Send POST data request

                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);

    //                                conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
    //                                conn.setRequestProperty("Accept","*/*");

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
                        String[] result = text.split(",");
                        if (!text.equals("failure"))
                        {
                            for (int j = 0; j<result.length;j++)
                            {
                                players.add(new PlayerDetails(result[j]));
                            }
                        }
                        else
                        {
                            SelectPlayers.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SelectPlayers.this, "Error: No users available nearby!", Toast.LENGTH_SHORT).show();
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

        Button bt = (Button) findViewById(R.id.selectAll);
        boxAdapter = new ListAdapter(this, players);

        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);

        bt.setOnClickListener(
                new android.view.View.OnClickListener() {


                    @Override
                    public void onClick(android.view.View arg0) {
                        String result = "Selected Product are :";
                        int count=0;
                        ArrayList<PlayerDetails> ChosenPlayers = new ArrayList<PlayerDetails>(boxAdapter.getCount());

                        for (PlayerDetails p : boxAdapter.getBox()) {
                            if (p.box){
                                ChosenPlayers.add(p);
                            }
                        }
                        Intent intent = new Intent(SelectPlayers.this,Timer.class);
                        intent.putExtra("chosenPlayers",ChosenPlayers);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(), result+"\n"+"Total Amount:="+totalAmount, Toast.LENGTH_LONG).show();
                    }
            });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_players, menu);
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
