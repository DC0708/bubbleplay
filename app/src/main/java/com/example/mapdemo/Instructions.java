package com.example.mapdemo;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Instructions extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TextView tx = (TextView)findViewById(R.id.about_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/GoodDog.otf");
        tx.setTypeface(custom_font);

        TextView tx1 = (TextView)findViewById(R.id.about_text);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/TheMillionMileMan.ttf");
        tx1.setTypeface(custom_font1);

        TextView tx3 = (TextView)findViewById(R.id.textView3);
        tx3.setText("1) Click on The Play Button. Chose one of the modes: SinglePlayer or MultiPlayer. Chose the level" +
                " you want to play: Become the Biggest or The Repulsor Challenge. Chose the Size of the boundary: Large, " +
                "Medium or Small.");

        TextView tx2 = (TextView)findViewById(R.id.textView2);
        tx2.setText("2) SinglePlayer Mode: In Become the Biggest, try to consume all the Snack Bubbles (Yellow)" +
                " that are smaller than you. When you become the biggest out of all the Snack bubbles, you win!" +
                " If one of the Snack bubbles consumes you or there comes a situation that you cannot become the biggest" +
                " from that point on, you Lose!");

        TextView tx4 = (TextView)findViewById(R.id.textView4);
        tx4.setText("3) SinglePlayer Mode: In the Repulsor Challenge, if you consume the Repeller bubble (Red), you win!" +
                " If one of the Snack Bubbles consumes you, you lose!") ;

        TextView tx5 = (TextView)findViewById(R.id.textView5);
        tx5.setText("4) MultiPlayer Mode: In Become the Biggest, try to consume all the Snack Bubbles (Yellow)" +
                " as well as the other player bubbles" +
                " that are smaller than you. When you become the biggest out of all the Player bubbles, you win!" +
                " If one of the Snack or Player bubbles consumes you, you lose!") ;

        TextView tx6 = (TextView)findViewById(R.id.textView6);
        tx6.setText("5) MultiPlayer Mode: If you catch the Repeller Bubble before anybody else, you win! " +
                "If any of the Snack bubbles or the Player bubbles consumes you, you lose!") ;

        TextView tx7 = (TextView)findViewById(R.id.textView7);
        tx7.setText("6) If the Player goes outside the boundary due to some reason, he will be given a time " +
                "window to come back inside. If he fails to do so, he will lose the game.") ;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instructions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
