package com.example.mapdemo;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class pregame extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame);

        String accepted = "";

        final TextView names = (TextView) findViewById(R.id.names);

        if (getIntent().getExtras().containsKey("accepted"))
        {
            accepted = getIntent().getExtras().getString("accepted");
            names.setText(accepted);
        }
        else
        {
            names.setText("");
        }

        final TextView tim = (TextView) findViewById(R.id.tim);;

           //tim.setText(getIntent().getExtras().getString("challengeID"));

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                tim.setText("Game will start in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                Toast.makeText(pregame.this, "Start the game!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(pregame.this,MainActivity.class));


            }

        }.start();


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
