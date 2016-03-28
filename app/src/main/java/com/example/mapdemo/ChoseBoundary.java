/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class ChoseBoundary extends ActionBarActivity {

    ImageButton small;
    ImageButton mdium;
    ImageButton large;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onStart(){

        super.onStart();
    }

    @Override
    protected void onRestart(){

        super.onRestart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chose_boundary);

        //     toolbar = (Toolbar) findViewById(R.id.toolbar);
        //     setSupportActionBar(toolbar);
        mediaPlayer = MediaPlayer.create(this, R.raw.gamebubble);

        small = (ImageButton) findViewById(R.id.imageButton);
        mdium = (ImageButton) findViewById(R.id.imageButton1);
        large = (ImageButton) findViewById(R.id.imageButton2);

        TextView tx = (TextView)findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        Intent temp = getIntent();
        final String gamemode = temp.getStringExtra("gamemode");
        final String playermode = temp.getStringExtra("playermode");
        final Intent i1 = new Intent(ChoseBoundary.this, SelectPlayers.class);
        final Intent i2 = new Intent(ChoseBoundary.this, Controller1.class);

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (playermode.equals("multiple")) {
                i1.putExtra("Boundary", "Small");
                i1.putExtra("gamemode", gamemode);
                i1.putExtra("playermode", playermode);
                startActivity(i1);
            }
            else {
                i2.putExtra("Boundary", "Small");
                i2.putExtra("gamemode", gamemode);
                i2.putExtra("playermode", playermode);
                startActivity(i2);
            }




    //        finish();
            return;
            }
        });

        mdium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (playermode.equals("multiple")) {
                    i1.putExtra("Boundary", "Medium");
                    i1.putExtra("gamemode", gamemode);
                    i1.putExtra("playermode", playermode);
                    startActivity(i1);
                }
                else {
                    i2.putExtra("Boundary", "Medium");
                    i2.putExtra("gamemode", gamemode);
                    i2.putExtra("playermode", playermode);
                    startActivity(i2);
                }
  //              finish();
                return;
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playermode.equals("multiple")) {
                    i1.putExtra("Boundary", "Large");
                    i1.putExtra("gamemode", gamemode);
                    i1.putExtra("playermode", playermode);
                    startActivity(i1);
                }
                else {
                    i2.putExtra("Boundary", "Large");
                    i2.putExtra("gamemode", gamemode);
                    i2.putExtra("playermode", playermode);
                    startActivity(i2);
                }
//                finish();
                return;
            }
        });


    }


    @Override
    protected void onResume(){

        super.onResume();
    }

    @Override
    protected void onPause(){

        super.onPause();
    }

    @Override
    protected void onStop(){

        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


}
