package com.example.mapdemo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import static com.example.mapdemo.CommonUtilities.SENDER_ID;


public class Register extends ActionBarActivity {

    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;


    TextView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton submit = (ImageButton) findViewById(R.id.reg_submit);

        user = (TextView) findViewById(R.id.game_name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        user.setTypeface(custom_font);

        final EditText username = (EditText) findViewById(R.id.reg_username);

        final EditText email = (EditText) findViewById(R.id.reg_email);

        final EditText password = (EditText) findViewById(R.id.reg_password);


        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Register.this,
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
        while(regId1.length()==0)
             regId1 = GCMRegistrar.getRegistrationId(this);

        final String regId = regId1;
        GCMRegistrar.register(this, SENDER_ID);
        Log.d("registration id : ",regId);
        submit.setOnClickListener(new android.view.View.OnClickListener() {


            @Override
            public void onClick(android.view.View arg0) {

                if (username.getText().length()!=0 && email.getText().length()!=0 && password.getText().length()!=0)
                {

                    ProgressDialog dialog = new ProgressDialog(Register.this); // this = YourActivity
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Registering. Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    new Thread(new Runnable() {
                        public void run() {


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
                                        // On server creates a new user
                                        //  ServerUtilities.register(context, name, email, regId);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        mRegisterTask = null;
                                    }

                                };
                                mRegisterTask.execute(null, null, null);
                            }
                            // Get user defined values
                            //System.out.println("Trying to connect!");
                            String data = "";
                            // Create data variable for sent values to server
                            try {

                                data = URLEncoder.encode("username", "UTF-8")
                                        + "=" + URLEncoder.encode(username.getText().toString(), "UTF-8");

                                data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                                        + URLEncoder.encode(email.getText().toString(), "UTF-8");

                                data += "&" + URLEncoder.encode("password", "UTF-8")
                                        + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");

                                data += "&" + URLEncoder.encode("appid", "UTF-8")
                                        + "=" + URLEncoder.encode(regId, "UTF-8");


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
                                Log.d("yoo:","in try for register!" + username.getText().toString() +
                                        email.getText().toString() + password.getText().toString());
                                URL url = new URL("http://10.1.33.78/BubblePlayServer/register.php");

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
                                if (text.equals("success"))
                                {
                                    SharedPreferences pref;
                                    SharedPreferences.Editor editor;
                                    // GCMRegistrar.setRegisteredOnServer(getApplicationContext(), true);

                                    pref = getApplicationContext().getSharedPreferences("UserSession",0);
                                    editor = pref.edit();
                                    editor.putBoolean("IsLoggedIn",true);
                                    editor.putString("username",username.getText().toString());
                                    editor.putString("email",email.getText().toString());
                                    editor.commit();

                                    startActivity(new Intent(Register.this, MainActivity.class));
                                }
                                else
                                {
                                    Register.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Register.this, "Error: " + text, Toast.LENGTH_SHORT).show();
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
                else if (username.getText().length()==0)
                {
                    Toast.makeText(Register.this,"Enter Username",Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().length()==0)
                {
                    Toast.makeText(Register.this,"Enter Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Register.this,"Enter Password",Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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