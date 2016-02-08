package com.example.mapdemo;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tx = (TextView)findViewById(R.id.about_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        TextView tx1 = (TextView)findViewById(R.id.about_text);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/TheMillionMileMan.ttf");
        tx1.setTypeface(custom_font1);

        TextView tx3 = (TextView)findViewById(R.id.textView3);
        tx3.setText("1) This Game aims at developing a platform to promote exer gaming" +
                ". Exer Games are video games that are a form of exercise. The prime motive is to promote a healthy and " +
                "active lifestyle in a fun way.");

        TextView tx2 = (TextView)findViewById(R.id.textView2);
        tx2.setText("2) We expect the user playing the game to get fitter day by day while playing the game." +
                " The game is mainly focussed on Obese children of age 10-20 years.");

        TextView tx4 = (TextView)findViewById(R.id.textView4);
        tx4.setText("3) Game Elements: \n\n i) Game Boundary: A square blue lined boundary is constructed with the " +
                "player in the center.\n\n" +
                " ii) Player Bubble: The player is shown as a pink bubble and is at the center of the square as the game starts." +
                " As the player moves, this Bubble also moves.\n\n" +
                " iii) Snack Bubble: These bubbles are shown as yellow color and eating this bubble having smaller size than the Player " +
                "will result in the Player becoming stronger and bigger. However if a bigger snack bubble consumes The Player, " +
                "the player will end up losing the game.\n\n" +
                " iv) Junk Bubble:These bubbles are shown as green color and eating a junk Bubble would result in reduced size of the " +
                "player Bubble. If a larger junk Bubble consumes you, you will end up losing the game.\n\n" +
                " v) Repeller Bubble: These bubbles are shown as red color and when a player bubble comes near a repeller, the repeller " +
                "gets repelled from the player bubble and runs away from it.\n\n" +
                " vi) Blind Spot: These are black colored holes shown on the map and a non player Bubble if goes into such a hole will " +
                "come out from any of the other blind spots.") ;

        TextView tx5 = (TextView)findViewById(R.id.textView5);
        //tx5.setVisibility(View.VISIBLE);

        TextView tx6 = (TextView)findViewById(R.id.textView6);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
}
