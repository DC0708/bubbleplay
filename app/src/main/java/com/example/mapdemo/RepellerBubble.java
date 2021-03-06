package com.example.mapdemo;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by DC0708 on 01-Feb-16.
 */
public class RepellerBubble {

    public double radius;

    public Centre center;

    public int boolTag;

    public final int mStrokeColor = Color.RED;

    public final int mFillColor = Color.RED;

    public int mWidth;

    public int dir;

    public double speed;

    public final double bubbleSpeed = 1.0 / 250000.0;

    public RepellerBubble(Boolean[][] isPlacedBubble,Model gamemodel, LatLng initialloc){

        Random r = new Random();
        int x = (r.nextInt(10)+0)%10;
        int y = (r.nextInt(10)+0)%10;
        this.dir = (r.nextInt(360)+0)%360;
        this.boolTag = 3;
        if (gamemodel.boundarytype.equals("Small")){
            this.radius = 0.4 + (0.01 * (double)((r.nextInt(39)+0)%40));
            System.out.println("Small bpundary repeller");
        }
        else if (gamemodel.boundarytype.equals("Medium")) {
            this.radius = 0.5 + (0.01 * (double) ((r.nextInt(49) + 0) % 50));
            System.out.println("Medium bpundary repeller");
        }else {
            this.radius = 0.6 + (0.01 * (double) ((r.nextInt(59) + 0) % 60));
            System.out.println("Large bpundary repeller");
        }
        //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
        while(isPlacedBubble[x][y]) {

            x = (r.nextInt(10)+0)%10;
            y = (r.nextInt(10)+0)%10;

        }
        if(gamemodel.boundarytype.equals("Small Boundary") || gamemodel.boundarytype.equals("Medium Boundary")) {
//            rCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), 0.7 * radius, dir, tag, 0.5*bubbleSpeed));
            this.radius = 0.7 * radius;
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12,(initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.speed = 0.5*bubbleSpeed;

        }
        else{


            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12,(initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.speed = 0.5*bubbleSpeed;

            // mCircles.add(circles.get(i));
        }

        isPlacedBubble[x][y]=true;



    }


}
