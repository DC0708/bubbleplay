package com.example.mapdemo;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by DC0708 on 01-Feb-16.
 */
public class SnackBubble {


    public double radius;

    public Centre center;

    public int boolTag;

    public int dir;

    public int mStrokeColor = Color.YELLOW ;

    public final int mFillColor = Color.YELLOW;

    public int mWidth;

    public double speed;
    final double bubbleSpeed = 1.0/250000.0;


    public SnackBubble(double radii, int direction,  double speeds,LatLng loc ){


        this.dir = direction;
        this.boolTag=1;
        this.center = new Centre(loc.latitude,loc.longitude);
        this.speed = speeds ;
        this.radius = radii;
    }

    public SnackBubble(Boolean[][] isPlacedBubble,int count,Model gamemodel,LatLng initialloc){

        Random r =new Random();

        int x = (r.nextInt(10)+0)%10;
        int y = (r.nextInt(10)+0)%10;
        this.dir = (r.nextInt(360)+0)%360;
        this.boolTag = 1;
        this.radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

        //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
        while(isPlacedBubble[x][y]) {

            x = (r.nextInt(10)+0)%10;
            y = (r.nextInt(10)+0)%10;

        }

        if(gamemodel.boundarytype.equals("Small") || gamemodel.boundarytype.equals("Medium")){
        /*circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), 0.7*radius,dir,tag,0.5*bubbleSpeed));*/
            this.radius = 0.7 * radius;
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12,(initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.speed = 0.5*bubbleSpeed;
            /** place bubble on the map **/

            isPlacedBubble[x][y]=true;
        }
        else{

            /** place bubble on the map **/
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12,(initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.speed = 0.5*bubbleSpeed;

            isPlacedBubble[x][y]=true;
        }


        /** Place the bubble on the map with its size depending on the boundary **/
    }

}
