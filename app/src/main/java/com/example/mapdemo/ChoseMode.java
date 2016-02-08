package com.example.mapdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;


public class ChoseMode extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_mode);

        TextView tx = (TextView)findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        ImageButton biggest;
        ImageButton repulsor;


        biggest = (ImageButton)findViewById(R.id.biggest);
        repulsor = (ImageButton)findViewById(R.id.repulsor);

        biggest.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(android.view.View v) {

                Log.d("gjygyf", "fhfdhtct");

                //               Intent myIntent = new Intent(MainActivity.this, TransitionPlayers.class);
                //              myIntent.putExtra("name",username);
                //              Toast.makeText(MainActivity.this,"Play button chosen",Toast.LENGTH_SHORT).show();
                //mediaPlayer.start();
                Intent i = getIntent();
                String s = i.getStringExtra("playermode");
                Intent i1 = new Intent(ChoseMode.this, ChoseBoundary.class);
                i1.putExtra("playermode",s);
                i1.putExtra("gamemode","biggest");
                startActivity(i1);
 //               finish();
                return;
            }
        });


        repulsor.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(android.view.View v) {

                Log.d("gjygyf", "fhfdhtct");

                //               Intent myIntent = new Intent(MainActivity.this, TransitionPlayers.class);
                //              myIntent.putExtra("name",username);
                //              Toast.makeText(MainActivity.this,"Play button chosen",Toast.LENGTH_SHORT).show();
                //mediaPlayer.start();
                Intent i = getIntent();
                String s = i.getStringExtra("playermode");
                Intent i1 = new Intent(ChoseMode.this, ChoseBoundary.class);
                i1.putExtra("playermode",s);
                i1.putExtra("gamemode","repulsor");
                startActivity(i1);
   //             finish();
                return;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chose_mode, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //   Toast.makeText(getApplicationContext(),"16. onDestroy()", Toast.LENGTH_SHORT).show();
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
