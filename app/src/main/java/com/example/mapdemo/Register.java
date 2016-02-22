package com.example.mapdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class Register extends ActionBarActivity {

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


        submit.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(android.view.View arg0) {

                if (username.getText().length()!=0 && email.getText().length()!=0 && password.getText().length()!=0)
                {

                    new Thread(new Runnable() {
                        public void run() {



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
                                URL url = new URL("http://10.1.13.10/BubblePlayServer/register.php");

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
                                    SharedPreferences pref;
                                    SharedPreferences.Editor editor;
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
}
