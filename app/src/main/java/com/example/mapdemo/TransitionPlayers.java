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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class TransitionPlayers extends ActionBarActivity {

    ImageButton FAB;
    ImageButton FAB1;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition_players);
        final Intent myintent = getIntent();
        mediaPlayer = MediaPlayer.create(this, R.raw.gamebubble);

        TextView tx = (TextView)findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        //     toolbar = (Toolbar) findViewById(R.id.toolbar);
        //     setSupportActionBar(toolbar);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB1 = (ImageButton) findViewById(R.id.imageButton1);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(TransitionPlayers.this,"Single Player Chosen",Toast.LENGTH_SHORT).show();

                mediaPlayer.start();

                String username = myintent.getStringExtra("name");

                Intent myIntent = new Intent(TransitionPlayers.this, ChoseMode.class);
                myIntent.putExtra("name",username);
                myIntent.putExtra("playermode","single");

                startActivity(myIntent);
 //               finish();
                return;
            }
        });

        FAB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.start();

                Toast.makeText(TransitionPlayers.this,"To be coded!!",Toast.LENGTH_SHORT).show();


            }
        });

    }

}
