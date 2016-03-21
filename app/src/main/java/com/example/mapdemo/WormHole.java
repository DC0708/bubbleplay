package com.example.mapdemo;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by DC0708 on 01-Feb-16.
 */
public class WormHole {

    public double radius;

    public Centre center;

    public int boolTag;

    public int mStrokeColor;

    public final int mFillColor = Color.BLACK;

    public int mWidth;


    public WormHole(double radii,LatLng loc ){


        this.boolTag=3;
        this.center = new Centre(loc.latitude,loc.longitude);
        this.radius = radii;
    }


    public WormHole(int[] xCoordinate, int[] yCoordinate,int count,Model gamemodel,LatLng initialloc){

        /**set up the constructor**/
        Random r = new Random();

        int x = (r.nextInt(10)+0)%10;
        int y = (r.nextInt(10)+0)%10;
        this.boolTag = 3;
        this.radius = 1;

        int check = 1;
        for (int j=0;j<count;j++)
        {
            if (Math.sqrt(Math.pow(x-xCoordinate[j],2) + Math.pow(y-yCoordinate[j],2)) < 4.5)
            {
                Log.d("WORM hole placement","Checkinnggg!!!");
                check = 0;
                break;
            }
        }

        //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
        while(check==0) {
            check = 1;
            x = (r.nextInt(10)+0)%10;
            y = (r.nextInt(10)+0)%10;
            for (int j=0;j<count;j++)
            {
                if (Math.sqrt(Math.pow(x-xCoordinate[j],2) + Math.pow(y-yCoordinate[j],2)) < 4.5)
                {
                    Log.d("WORM hole placement","Checkinnggg!!!");
                    check = 0;
                    break;
                }
            }


        }

        if(gamemodel.boundarytype.equals("Large")) {
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12, (initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.radius = 1.5 * radius;

            xCoordinate[count] = x;
            yCoordinate[count++] = y;
        }

        else if(gamemodel.boundarytype.equals("Medium")) {
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12, (initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);
            this.radius = 1.0 * radius;

            xCoordinate[count] = x;
            yCoordinate[count++] = y;
        }
        else{
        //    bCircles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12),0.6 * radius, dir, tag, 0));

            this.radius = 0.6 * radius;
            this.center = new Centre((initialloc.latitude - gamemodel.boundaryWidth / 2) + (x + 1) * gamemodel.boundaryWidth / 12, (initialloc.longitude - gamemodel.boundaryHeight / 2) + (y + 1) * gamemodel.boundaryHeight / 12);

            xCoordinate[count] = x;
            yCoordinate[count++] = y;

        }


        /** Place the bubble on the map with its size depending on the boundary **/

    }

}
