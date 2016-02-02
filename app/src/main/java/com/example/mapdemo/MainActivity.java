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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;

import com.facebook.login.widget.LoginButton;


/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends ActionBarActivity{

    private RadioGroup radioBoundaryGroup;
    private RadioButton radioBoundaryButton;
    private Button btnDisplay;

    private MediaPlayer mediaPlayer;
    Toolbar toolbar;
    ImageButton FAB;
    ImageButton FAB1;
    ImageButton FAB2;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.main);

        mediaPlayer = MediaPlayer.create(this, R.raw.gamebubble);

        //     toolbar = (Toolbar) findViewById(R.id.toolbar);
        //     setSupportActionBar(toolbar);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB1 = (ImageButton) findViewById(R.id.imageButton1);
        //FAB2 = (ImageButton) findViewById(R.id.imageButton2);
        TextView tx = (TextView)findViewById(R.id.textView);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");

        tx.setTypeface(custom_font);

        Log.d("hyugf", "fgdfgdrtsgrf");

        callbackManager = CallbackManager.Factory.create();
//        setContentView(R.layout.main);
  //      info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {



            @Override
            public void onSuccess(final LoginResult loginResult) {
/*                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
*/              username = loginResult.getAccessToken().getToken();

 //               Log.d("fb status",);


            }
                @Override
                public void onCancel () {
    //                info.setText("Login attempt canceled.");
                    Log.d("Cancel","sffds");
                }

                @Override
                public void onError (FacebookException err){
      //              info.setText("Login attempt failed.");
                    Log.d("Error","sffds");
                }

            });

        FAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d("gjygyf","fhfdhtct");

 //               Intent myIntent = new Intent(MainActivity.this, TransitionPlayers.class);
  //              myIntent.putExtra("name",username);
  //              Toast.makeText(MainActivity.this,"Play button chosen",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                startActivity(new Intent(MainActivity.this, TransitionPlayers.class));

            }
        });

        Log.d("lol",FAB.hasOnClickListeners()+" ");

        FAB1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                Toast.makeText(MainActivity.this,"About to be written",Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(this, ChoseBoundary.class));

            }
        });




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.id.menu_legal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_legal) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    *
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
/*    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.

        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView list = (ListView) findViewById(R.id.list);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        RadioGroup radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioBoundaryGroup.setVisibility(View.INVISIBLE);
        Button button = (Button) findViewById(R.id.btnDisplay);
        button.setVisibility(View.INVISIBLE);

        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setEmptyView(findViewById(R.id.empty));

      //  addListenerOnButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_legal ) {


         /*   btnDisplay = (Button) findViewById(R.id.btnDisplay);

            btnDisplay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // get selected radio button from radioGroup
                    int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioBoundaryButton = (RadioButton) findViewById(selectedId);

                 }


            }
        startActivity(new Intent(this, LegalInfoActivity.class));
        return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        Log.d("aalo111","yoo111");

        btnDisplay.setVisibility(View.VISIBLE);
        radioBoundaryGroup.setVisibility(View.VISIBLE);

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();
                Log.d("aalo","yo00o");

//                startActivity(new Intent(MainActivity.this, LegalInfoActivity.class));
                Log.d("aalo","yo1o");
                Intent i = new Intent(MainActivity.this, demo.activityClass);
                String strName = null;
                i.putExtra("Boundary", radioBoundaryButton.getText());
                //            DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);
//                startActivity(new Intent(MainActivity.this, TopView.class));
                startActivity(i);
                //startActivity(new Intent(MainActivity.this, MarkerDemoActivity.class));

                Log.d("aalo","yo2o");

                //return true;

            }

        });



    }

    public void addListenerOnButton() {

        radioBoundaryGroup = (RadioGroup) findViewById(R.id.radioSex);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);
        Log.d("aalo111","yoo111");

        btnDisplay.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {




                // get selected radio button from radioGroup
                int selectedId = radioBoundaryGroup.getCheckedRadioButtonId();
                Log.d("aalo","yoo");
                // find the radiobutton by returned id
                radioBoundaryButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(MainActivity.this,
                        radioBoundaryButton.getText(), Toast.LENGTH_SHORT).show();

            }

        });

    }

*/
}
