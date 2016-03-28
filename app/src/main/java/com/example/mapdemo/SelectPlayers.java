package com.example.mapdemo;

import android.app.ProgressDialog;
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

        Button bt = (Button) findViewById(R.id.selectAll);

        Thread thread;

        sp  = getApplicationContext().getSharedPreferences("UserSession",0);

        Log.d("sp ",sp + " ");

        if (sp.getBoolean("isLoggedIn",false)) {
            startActivity(new Intent(SelectPlayers.this, Login.class));
        }
        else
        {
            final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            Thread t = new Thread(new Runnable() {
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
                        URL url = new URL(CommonUtilities.SERVER_URL + "getAppID.php");

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
                        Log.d("builder is ",builder);
                        final String text = builder;
                        System.out.println("Text: "+text);
                        System.out.println("len: "+text.length());
                        String[] result = text.split(",");

                        if (!text.equals("failure"))
                        {
                            System.out.println("adding players");
                            for (int j = 0; j<result.length;j++)
                            {
                                String[] playr = result[j].split("\\.");
                                System.out.println("player size: " + playr.length);
                                Log.d("result is :", result[j]);
                                players.add(new PlayerDetails(playr[0],playr[1]));

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
                    System.out.println("size is: " + players.size());

//                    SelectPlayers.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                dialog.dismiss();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });




                }


            });

            t.start();

            try{
                t.join();
                dialog.dismiss();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            boxAdapter = new ListAdapter(getApplicationContext(), players);

            ListView lvMain = (ListView) findViewById(R.id.lvMain);
            lvMain.setAdapter(boxAdapter);

            bt.setOnClickListener(
                    new android.view.View.OnClickListener() {


                        @Override
                        public void onClick(android.view.View arg0) {
                            String result = "Selected Product are :";
                            int count=0;
                            String ChosenPlayers = "";
                            String appids = "";

                            for (PlayerDetails p : boxAdapter.getBox()) {
                                if (p.box){
                                    if (count==0) {
                                        ChosenPlayers += p.name;
                                        appids +=p.appID;
                                    }
                                    else {
                                        ChosenPlayers += "," + p.name;
                                        appids +="," + p.appID;
                                    }
                                    count++;
                                }
                            }
                            Intent intent = new Intent(SelectPlayers.this,Timer.class);
                            intent.putExtra("chosenPlayers",ChosenPlayers);
                            intent.putExtra("appID",appids);
                            intent.putExtra("Boundary", getIntent().getExtras().getString("Boundary"));
                            intent.putExtra("gamemode", getIntent().getExtras().getString("Boundary"));
                            intent.putExtra("playermode", getIntent().getExtras().getString("Boundary"));
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(), result+"\n"+"Total Amount:="+totalAmount, Toast.LENGTH_LONG).show();
                        }
                    });
        }




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
