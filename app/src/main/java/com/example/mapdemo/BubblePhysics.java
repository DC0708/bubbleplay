package com.example.mapdemo;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DC0708 on 17-Nov-15.
 */
public class BubblePhysics {

    private int mFillColor;

    //private List<DraggableCircle> circles = new ArrayList<DraggableCircle>(10);

//    private LatLng InitialLoc;

    public void OnCollisionBoundary(List<DraggableCircle> circles,LatLng InitialLoc,double height,double width){

        for(int i=0;i<circles.size();i++){
            // circles.g
            DraggableCircle cir = circles.get(i);
//                            Log.d("tagg",cir.toString());

            //            System.out.println(cir.circle.getCenter().latitude);
            LatLng loca =  cir.circle.getCenter();
            double latit = loca.latitude;
            double longit = loca.longitude;
            // Log.d("angle"," "+cir.direction);
            LatLng finalLoc = new LatLng(latit+Math.sin(Math.toRadians(cir.direction))/50000,longit+Math.cos(Math.toRadians(cir.direction))/50000);
            cir.circle.setCenter(finalLoc);
            //  Log.d("inital location", InitialLoc.latitude + " " + InitialLoc.longitude);
            // Log.d(" direction "," " +cir.direction);
            // Log.d("collision", latit + " " + longit + " "+ cir.direction + " " + (InitialLoc.latitude +width/2));
            if(latit > InitialLoc.latitude +width/2){
                cir.direction= 360-cir.direction;
                cir.circle.setCenter(new LatLng(InitialLoc.latitude +width/2,longit));

                //    Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(longit > InitialLoc.longitude +width/2){
                if(cir.direction<180)
                    cir.direction= 180-cir.direction;
                else
                    cir.direction = 540-cir.direction;
                cir.circle.setCenter(new LatLng(latit,InitialLoc.longitude +width/2));
                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(latit < InitialLoc.latitude -width/2){
                cir.direction= 360-cir.direction;

                cir.circle.setCenter(new LatLng(InitialLoc.latitude -width/2,longit));

                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }
            else if(longit < InitialLoc.longitude -width/2){
                if(cir.direction <180)
                    cir.direction= 180-cir.direction;
                else
                    cir.direction = 540-cir.direction;
                cir.circle.setCenter(new LatLng(latit,InitialLoc.longitude -width/2));

                //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
            }


        }


    }

    public void CollisionNonPlayerBubbles(List<DraggableCircle> circles,GoogleMap mMap){

        int check=0;
        /****** ----- Collision of non player bubbles ****/
        for(int i=0;i<circles.size();i++){

            for(int j=0;j<circles.size();j++){

                if(i!=j){
                    double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, circles.get(j).circle.getCenter().latitude, circles.get(j).circle.getCenter().longitude);

                    if(circles.get(i).radius+circles.get(j).radius>=Math.abs(distanceBwBubbles)){


                        double r1 = circles.get(i).radius;
                        double r2 = circles.get(j).radius;

                        double lat = r1>=r2?circles.get(i).circle.getCenter().latitude:circles.get(j).circle.getCenter().latitude;
                        double lon = r1>=r2?circles.get(i).circle.getCenter().longitude:circles.get(j).circle.getCenter().longitude;
                        int dir = r1>=r2?circles.get(i).direction:circles.get(j).direction;
                        int tag = r1>=r2?circles.get(i).bTag:circles.get(j).bTag;
                        double newRadius;
                        if (circles.get(i).bTag==circles.get(j).bTag)
                            newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                        else
                            newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                        //    Log.d("New Radius is"," "+newRadius);

                        //    Log.d("size of circles before removal", " "+ circles.size());
                        //circles.get(i).
                        if(i<j)
                            j-=1;
                        circles.get(i).circle.remove();

                        circles.remove(i);

                        circles.get(j).circle.remove();


                        circles.remove(j);
                        //  Log.d("size of circles", " "+ circles.size());
                        if (tag==1)
                            mFillColor = Color.YELLOW;
                        else
                            mFillColor = Color.GREEN;
                        circles.add(new DraggableCircle(new LatLng(lat,lon), newRadius,dir,tag,mMap));
                        check=1;
                        break;
                    }


                }


            }

            if(check==1)
                break;



        }



    }

    public void CollisionPlayerNonplayer(List<DraggableCircle> circles ,List<DraggableCircle> mCircles  ){

        if(mCircles.size()>0){
            DraggableCircle player = mCircles.get(mCircles.size()-1);

            for(int i =0 ; i <circles.size();i++){

                double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, mCircles.get(mCircles.size()-1).circle.getCenter().latitude, mCircles.get(mCircles.size()-1).circle.getCenter().longitude);

                if(circles.get(i).radius+mCircles.get(mCircles.size()-1).radius>=Math.abs(distanceBwBubbles)) {
                    Log.d("collision with player", " " + circles.get(i).bTag);
                    if (player.radius < circles.get(i).radius) {
                        int size = mCircles.size();
                        mCircles.get(size - 1).circle.remove();
                        mCircles.remove(size - 1);
                        break;
                    } else {
                        if (circles.get(i).bTag == 1) {
                            double r1 = player.radius;
                            double r2 = circles.get(i).radius;

                            double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);
                            player.circle.setRadius(newRadius);
                            circles.get(i).circle.remove();
                            circles.remove(i);
                            break;
                        } else if (circles.get(i).bTag == 2) {
                            double r1 = player.radius;
                            double r2 = circles.get(i).radius;

                            double newRadius = Math.pow(Math.abs(Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);
                            player.circle.setRadius(newRadius);
                            circles.get(i).circle.remove();
                            circles.remove(i);
                            break;
                        } else {

                        }

                    }

                }








            }




        }
        else{
          //   Toast.makeText(getApplicationContext(), "Game Over!!!", Toast.LENGTH_SHORT).show();
            //Intent i = new Intent(TopView.this, MainActivity.class);
            String strName = null;
            //i.putExtra("Boundary", radioBoundaryButton.getText());
            //            DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);
//                startActivity(new Intent(MainActivity.this, TopView.class));
            // startActivity(i);
        }


    }


    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist =  (earthRadius * c);

        return dist;
    }







}
