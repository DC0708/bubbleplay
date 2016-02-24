package com.example.mapdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;


public class EndGame extends ActionBarActivity {


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
        setContentView(R.layout.activity_end_game);

        TextView tx = (TextView)findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        TextView tx1 = (TextView)findViewById(R.id.game_over);
        tx1.setTypeface(custom_font);

        TextView tx2 = (TextView)findViewById(R.id.score);
        tx2.setTypeface(custom_font);

        ImageButton imageButton = (ImageButton)findViewById(R.id.main_menu);

        Intent i = getIntent();
        String score = i.getStringExtra("totalscore");
        if(i.getStringExtra("gameresult").equals("won"))
            tx2.setText("You Won!!\nScore: " + score);
        else
            tx2.setText("You Lost!!\nScore: " + score);

        imageButton.setOnClickListener(new android.view.View.OnClickListener(){
            @Override
            public void onClick(android.view.View v) {

                Log.d("gjygyf", "fhfdhtct");

                //               Intent myIntent = new Intent(MainActivity.this, TransitionPlayers.class);
                //              myIntent.putExtra("name",username);
                //              Toast.makeText(MainActivity.this,"Play button chosen",Toast.LENGTH_SHORT).show();
                //mediaPlayer.start();

                startActivity(new Intent(EndGame.this, MainActivity.class));
         //       finish();
                return;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
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
