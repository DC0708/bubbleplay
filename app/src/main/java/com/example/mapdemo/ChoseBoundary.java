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

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent i = new Intent(ChoseBoundary.this, Controller1.class);
            i.putExtra("Boundary", "Small");
            i.putExtra("gamemode",gamemode);
            i.putExtra("playermode",playermode);

            Log.d("gamemode is 1",gamemode);
            Toast.makeText(ChoseBoundary.this,"Small Boundary chosen",Toast.LENGTH_SHORT).show();
            mediaPlayer.start();

            startActivity(i);
    //        finish();
            return;
            }
        });

        mdium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ChoseBoundary.this, Controller1.class);
                i.putExtra("Boundary", "Medium");
                i.putExtra("gamemode",gamemode);
                i.putExtra("playermode",playermode);
                Toast.makeText(ChoseBoundary.this,"Medium Boundary chosen",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
                Log.d("gamemode is ",gamemode);
                startActivity(i);
  //              finish();
                return;
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ChoseBoundary.this, Controller1.class);
                i.putExtra("Boundary", "Large");
                i.putExtra("gamemode",gamemode);
                i.putExtra("playermode",playermode);
                Log.d("gamemode is 3",gamemode);

                Toast.makeText(ChoseBoundary.this,"Large Boundary chosen",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                startActivity(i);
//                finish();
                return;
            }
        });


    }

}
