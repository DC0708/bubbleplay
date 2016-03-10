package com.example.mapdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.example.mapdemo.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.mapdemo.CommonUtilities.EXTRA_MESSAGE;


public class Login extends AppCompatActivity {

    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;


    TextView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton submit = (ImageButton) findViewById(R.id.login_button);

        final EditText email = (EditText) findViewById(R.id.login_email);

        final EditText password = (EditText) findViewById(R.id.login_password);

        user = (TextView) findViewById(R.id.game_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        user.setTypeface(custom_font);


        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Login.this,
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
        final String regId = GCMRegistrar.getRegistrationId(this);


        Log.d("registration id : ",regId);

        submit.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(android.view.View arg0) {

                if (email.getText().length()!=0 && password.getText().length()!=0)
                {

                    new Thread(new Runnable() {
                        public void run() {



                            // Get user defined values
                            //System.out.println("Trying to connect!");
                            String data = "";
                            // Create data variable for sent values to server
                            try {

                                data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                                        + URLEncoder.encode(email.getText().toString(), "UTF-8");

                                data += "&" + URLEncoder.encode("password", "UTF-8")
                                        + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");
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
                                URL url = new URL("http://10.1.42.193/BubblePlayServer/login.php");

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
                                    editor = pref.edit();
                                    editor.putBoolean("IsLoggedIn",true);
                                    editor.putString("appid",regId);
                                    editor.putString("username",response[1].toString());
                                    editor.putString("email",email.getText().toString());
                                    editor.commit();
                                    System.out.println(response[1].toString());
                                    System.out.println("Logging in!");
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                }
                                else
                                {
                                    Login.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Login.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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
                else if (email.getText().length()==0)
                {
                    Toast.makeText(Login.this,"Enter Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Login.this,"Enter Password",Toast.LENGTH_SHORT).show();
                }

            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
